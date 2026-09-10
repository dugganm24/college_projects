/**
 * main.c
 *
 * ECE 3849 Lab 0 Starter Project
 * Gene Bogdanov    10/18/2017
 *
 * This version is using the new hardware for B2017: the EK-TM4C1294XL LaunchPad with BOOSTXL-EDUMKII BoosterPack.
 *
 */

#include <stdint.h>
#include <stdbool.h>
#include "driverlib/fpu.h"
#include "driverlib/sysctl.h"
#include "driverlib/interrupt.h"
#include "Crystalfontz128x128_ST7735.h"
#include <stdio.h>
#include "buttons.h"
#include <math.h>
#include "inc/hw_memmap.h"
#include "driverlib/gpio.h"
#include "driverlib/pwm.h"
#include "driverlib/pin_map.h"
#include "sampling.h"
#include "driverlib/timer.h"
#define PWM_FREQUENCY 20000 // PWM frequency = 20 kHz
#define PIXELS_PER_DIV 20


uint32_t gSystemClock; // [Hz] system clock frequency
volatile uint32_t gTime = 8345; // time in hundredths of a second

float load=0;
float unload=0;
float cpu_load=0.0;

char str[50];


uint32_t CPULoad(void);


int main(void)
{
    IntMasterDisable();

    // Enable the Floating Point Unit, and permit ISRs to use it
    FPUEnable();
    FPULazyStackingEnable();

    // Initialize the system clock to 120 MHz
    gSystemClock = SysCtlClockFreqSet(SYSCTL_XTAL_25MHZ | SYSCTL_OSC_MAIN | SYSCTL_USE_PLL | SYSCTL_CFG_VCO_480, 120000000);

    // configure M0PWM2, at GPIO PF2, BoosterPack 1 header C1 pin 2
    SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOF);
    GPIOPinTypePWM(GPIO_PORTF_BASE, GPIO_PIN_2);
    GPIOPinConfigure(GPIO_PF2_M0PWM2);
    GPIOPadConfigSet(GPIO_PORTF_BASE, GPIO_PIN_2,
    GPIO_STRENGTH_2MA, GPIO_PIN_TYPE_STD);
    // configure the PWM0 peripheral, gen 1, outputs 2 and 3
    SysCtlPeripheralEnable(SYSCTL_PERIPH_PWM0);
    // use system clock without division
    PWMClockSet(PWM0_BASE, PWM_SYSCLK_DIV_1);
    PWMGenConfigure(PWM0_BASE, PWM_GEN_1,
    PWM_GEN_MODE_DOWN | PWM_GEN_MODE_NO_SYNC);
    PWMGenPeriodSet(PWM0_BASE, PWM_GEN_1,
    roundf((float)gSystemClock/PWM_FREQUENCY));
    PWMPulseWidthSet(PWM0_BASE, PWM_OUT_2,
    roundf((float)gSystemClock/PWM_FREQUENCY*0.4f));
    PWMOutputState(PWM0_BASE, PWM_OUT_2_BIT, true);
    PWMGenEnable(PWM0_BASE, PWM_GEN_1);

    Crystalfontz128x128_Init(); // Initialize the LCD display driver
    Crystalfontz128x128_SetOrientation(LCD_ORIENTATION_UP); // set screen orientation

    ADC_Init();
    ButtonInit();

    tContext sContext;
    GrContextInit(&sContext, &g_sCrystalfontz128x128); // Initialize the grlib graphics context
    GrContextFontSet(&sContext, &g_sFontFixed6x8); // select font

    uint32_t sample[LCD_HORIZONTAL_MAX];
    int i = 0;
    int y = 0;
    int prev_y = 0;
    int rising = 1;
    int trigger;
    int voltsPerDiv = 4;
    int fVoltsPerDiv[] = {0.1,0.2,0.5,1,2};


    DataType button;

    const char * const gVoltageScaleStr[] = {
        "100 mV", "200 mV", "500 mV", "1V", "2V"
    };

    unload = CPULoad();

    volatile uint16_t buffer[ADC_BUFFER_SIZE];

    // full-screen rectangle
    tRectangle rectFullScreen = {0, 0, GrContextDpyWidthGet(&sContext)-1, GrContextDpyHeightGet(&sContext)-1};

    IntMasterEnable();

    while (true) {
      load = CPULoad(); //measure interrupts enabled for every frame

      cpu_load = (1.0 - load/unload)*100; //calculate CPU utilization

        while(fifo_get(&button)){
            switch(button){
            case '1':
                voltsPerDiv = voltsPerDiv + 1 > 4 ? 4 : voltsPerDiv + 1;
                break;
            case '2':
                voltsPerDiv = --voltsPerDiv <= 0 ? 0 : voltsPerDiv--;
                break;
            case '3':
                rising = !rising;
                break;
            }
        }

        GrContextForegroundSet(&sContext, ClrBlack);
        GrRectFill(&sContext, &rectFullScreen); // fill screen with black
        GrContextForegroundSet(&sContext, ClrBlue);
        int j=0;
        for(j = -3; j < 4; j++){
            GrLineDrawH(&sContext, 0, LCD_HORIZONTAL_MAX - 1, LCD_VERTICAL_MAX/2 + j * PIXELS_PER_DIV);
            GrLineDrawV(&sContext, LCD_VERTICAL_MAX /2 + j * PIXELS_PER_DIV, 0, LCD_HORIZONTAL_MAX-1);
        }

        trigger = rising ? RisingTrigger(): FallingTrigger();
        float fScale = (VIN_RANGE * PIXELS_PER_DIV)/((1 << ADC_BITS) * fVoltsPerDiv[voltsPerDiv]);

           GrContextForegroundSet(&sContext, ClrYellow);
           for(i = 0; i < LCD_HORIZONTAL_MAX - 1; i++) {
               sample[i] = gADCBuffer[ADC_BUFFER_WRAP(trigger - LCD_HORIZONTAL_MAX/2 + i)];
           }
           for(i = 0; i < LCD_HORIZONTAL_MAX - 1; i++) {
              y = LCD_VERTICAL_MAX/2 - (int)roundf(fScale * ((int)sample[i]-ADC_OFFSET)); //calculate y value
              GrLineDraw(&sContext, i, prev_y, i + 1, y); //line from last y value to next y value
              prev_y = y; //set new y value to old y value for next iteration
          }

           GrContextForegroundSet(&sContext, ClrWhite);
           if(rising){
               GrLineDraw(&sContext, 105, 10, 115, 10);
               GrLineDraw(&sContext, 115, 10, 115, 0);
               GrLineDraw(&sContext, 115, 0, 125, 0);
               GrLineDraw(&sContext, 112, 6, 115, 2);
               GrLineDraw(&sContext, 115, 2, 118, 6);
           }

           if(!rising){
               GrLineDraw(&sContext, 105, 10, 115, 10);
               GrLineDraw(&sContext, 115, 10, 115, 0);
               GrLineDraw(&sContext, 115, 0, 125, 0);
               GrLineDraw(&sContext, 112, 3, 115, 7);
               GrLineDraw(&sContext, 115, 7, 118, 3);
            }

           if(!checkTrigger){
               GrLineDraw(&sContext, 105, 10, 115, 10);
               GrLineDraw(&sContext, 115, 10, 115, 0);
               GrLineDraw(&sContext, 115, 0, 125, 0);
           }


           GrStringDraw(&sContext, "20 us", -1, 4, 0, false);
           GrStringDraw(&sContext, gVoltageScaleStr[voltsPerDiv], -1, 50, 0, false);
           int decimal = (int)cpu_load%10;
           int integer = (int)cpu_load;
           snprintf(&str, 40, "CPU Load = %d.%d%%", integer, decimal);
           GrStringDraw(&sContext, str, -1, 1, 120, false);
           GrFlush(&sContext);
}
}


uint32_t CPULoad(void){
      float i = 0;
      TimerIntClear(TIMER3_BASE, TIMER_TIMA_TIMEOUT);
      TimerEnable(TIMER3_BASE,TIMER_A); //start one-shot timer

      while (!(TimerIntStatus(TIMER3_BASE, false) & TIMER_TIMA_TIMEOUT)) {
           i++;
       }
        return i;
}


