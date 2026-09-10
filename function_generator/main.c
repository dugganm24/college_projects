
#include <msp430.h>
#include "peripherals.h"

long unsigned int global_time_cnt;
long unsigned int countdown;

void runTimerA2(void){
    TA2CTL = TASSEL_1 | MC_1 | ID_0;
    TA2CCR0 = 163;
    TA2CCTL0 = CCIE;
}

void timerA2freq(unsigned int frequency){
    TA2CTL = TASSEL_1 | MC_1 | ID_0;
    TA2CCR0 = frequency;
    TA2CCTL0 = CCIE;
}

void timerA2saw(){
    TA2CTL = TASSEL_1 | MC_1 | ID_0;
    TA2CCR0 = 21;
    TA2CCTL0 = CCIE;
}

void stopTimerA2(){
    TA2CCTL0 = 0;
}

#pragma vector = TIMER2_A0_VECTOR
__interrupt void Timer_A2_ISR(void){
    global_time_cnt++;
    countdown--;
}

void DACinit(void)
{
    // Configure LDAC and CS for digital IO outputs
    DAC_PORT_LDAC_SEL &= ~DAC_PIN_LDAC;
    DAC_PORT_LDAC_DIR |=  DAC_PIN_LDAC;
    DAC_PORT_LDAC_OUT |= DAC_PIN_LDAC; // Deassert LDAC

    DAC_PORT_CS_SEL   &= ~DAC_PIN_CS;
    DAC_PORT_CS_DIR   |=  DAC_PIN_CS;
    DAC_PORT_CS_OUT   |=  DAC_PIN_CS;  // Deassert CS
}

void DACSetValue(unsigned int dac_code)
{
    // Start the SPI transmission by asserting CS (active low)
    // This assumes DACInit() already called
    DAC_PORT_CS_OUT &= ~DAC_PIN_CS;

    // Write in DAC configuration bits. From DAC data sheet
    // 3h=0011 to highest nibble.
    // 0=DACA, 0=buffered, 1=Gain=1, 1=Out Enbl
    dac_code |= 0x3000;   // Add control bits to DAC word

    uint8_t lo_byte = (unsigned char)(dac_code & 0x00FF);
    uint8_t hi_byte = (unsigned char)((dac_code & 0xFF00) >> 8);

    // First, send the high byte
    DAC_SPI_REG_TXBUF = hi_byte;

    // Wait for the SPI peripheral to finish transmitting
    while(!(DAC_SPI_REG_IFG & UCTXIFG)) {
        _no_operation();
    }

    // Then send the low byte
    DAC_SPI_REG_TXBUF = lo_byte;

    // Wait for the SPI peripheral to finish transmitting
    while(!(DAC_SPI_REG_IFG & UCTXIFG)) {
        _no_operation();
    }

    // We are done transmitting, so de-assert CS (set = 1)
    DAC_PORT_CS_OUT |=  DAC_PIN_CS;

    
    DAC_PORT_LDAC_OUT &= ~DAC_PIN_LDAC;  // Assert LDAC
    __delay_cycles(10);                 // small delay
    DAC_PORT_LDAC_OUT |=  DAC_PIN_LDAC;  // De-assert LDAC
}

void setADC12_scroll(){
    P8SEL &= ~BIT0;
    P8DIR |= BIT0;
    P8OUT |= BIT0;

    P6SEL |= BIT0;
    REFCTL0 &= ~REFMSTR;

    ADC12CTL0 = ADC12SHT0_9 | ADC12ON;

    ADC12CTL1 = ADC12SHP;

    ADC12MCTL0 = ADC12SREF_0 + ADC12INCH_0;

    __delay_cycles(100);
    ADC12CTL0 |= ADC12ENC;
}

unsigned int potVal(){
    unsigned int in_value;
    ADC12CTL0 &= ~ADC12SC;
    ADC12CTL0 |= ADC12SC;

    while (ADC12CTL1 & ADC12BUSY)
        __no_operation();
    in_value = ADC12MEM0 & 0x0FFF;

    __no_operation();


    return in_value;
}



enum State {WELCOME, DC, SQUARE, SAW, TRIANGLE};


int main(){

    WDTCTL = WDTPW | WDTHOLD;
    _BIS_SR(GIE);

    configDisplay();
    configButton();
    DACinit();
    setADC12_scroll();
    Graphics_clearDisplay(&g_sContext);

    enum State state = WELCOME;

    int button;
    unsigned int DCvolts;
    unsigned int sawWave;
    unsigned int triangleWave;
    unsigned int freq;
    unsigned int up = 1;
    unsigned int codes[] = {0, 1489};
    long unsigned int pause;
    int ADCval;

    while(1){
        switch(state){

        case WELCOME:
            stopTimerA2();
            global_time_cnt = 0;
            button = 0;
            DCvolts = 0;
            sawWave = 0;
            triangleWave = 0;
            freq = 0;
            up = 1;



            //Graphics_clearDisplay(&g_sContext);

            Graphics_drawStringCentered(&g_sContext, "Function", AUTO_STRING_LENGTH, 48, 15, TRANSPARENT_TEXT);
            Graphics_drawStringCentered(&g_sContext, "Generator", AUTO_STRING_LENGTH, 48, 25, TRANSPARENT_TEXT);
            Graphics_drawStringCentered(&g_sContext, "S1: DC", AUTO_STRING_LENGTH, 48, 45, TRANSPARENT_TEXT);
            Graphics_drawStringCentered(&g_sContext, "S2: Square", AUTO_STRING_LENGTH, 48, 55, TRANSPARENT_TEXT);
            Graphics_drawStringCentered(&g_sContext, "S3: Sawtooth", AUTO_STRING_LENGTH, 48, 65, TRANSPARENT_TEXT);
            Graphics_drawStringCentered(&g_sContext, "S4: Triangle", AUTO_STRING_LENGTH, 48, 75, TRANSPARENT_TEXT);
            Graphics_flushBuffer(&g_sContext);


            button = press();

            if(button == 1){
                state = DC;
                Graphics_clearDisplay(&g_sContext);
                break;
            }
            if(button == 2){
                state = SQUARE;
                Graphics_clearDisplay(&g_sContext);
                break;
            }
            if(button == 3){
                state = SAW;
                Graphics_clearDisplay(&g_sContext);
                break;
            }
            if(button == 4){
                state = TRIANGLE;
                Graphics_clearDisplay(&g_sContext);
                break;
            }

            break;


        case DC:
            Graphics_drawStringCentered(&g_sContext, "DC Mode", AUTO_STRING_LENGTH, 48, 50, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "Press 1", AUTO_STRING_LENGTH, 48, 70, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "to return", AUTO_STRING_LENGTH, 48, 80, OPAQUE_TEXT);
            Graphics_flushBuffer(&g_sContext);

            while(1){
                DCvolts = potVal();
                DACSetValue(DCvolts);



                button = press();
                if(button == 1){
                    Graphics_clearDisplay(&g_sContext);
                    state = WELCOME;
                    break;
                }
            }
            break;

        case SQUARE:
            Graphics_drawStringCentered(&g_sContext, "Square Mode", AUTO_STRING_LENGTH, 48, 50, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "Press 1", AUTO_STRING_LENGTH, 48, 70, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "to return", AUTO_STRING_LENGTH, 48, 80, OPAQUE_TEXT);
            Graphics_flushBuffer(&g_sContext);

            runTimerA2();

            while(1){
                DCvolts = potVal();
                codes[1] = DCvolts;
                DACSetValue(codes[global_time_cnt & 1]);

                button = press();
                if(button == 1){
                    Graphics_clearDisplay(&g_sContext);
                    state = WELCOME;
                    break;
                }
            }
            break;

        case SAW:
            Graphics_drawStringCentered(&g_sContext, "Sawtooth Mode", AUTO_STRING_LENGTH, 48, 50, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "Press 1", AUTO_STRING_LENGTH, 48, 70, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "to return", AUTO_STRING_LENGTH, 48, 80, OPAQUE_TEXT);
            Graphics_flushBuffer(&g_sContext);

            timerA2saw();

            while(1){
                sawWave = global_time_cnt * 74;
                if(sawWave >= 1480){
                    sawWave = 0;
                    global_time_cnt = 0;
                }

                DACSetValue(sawWave);

                button = press();
                if(button == 1){
                    Graphics_clearDisplay(&g_sContext);
                    state = WELCOME;
                    break;
                }
            }
            break;

        case TRIANGLE:
            Graphics_drawStringCentered(&g_sContext, "Triangle Mode", AUTO_STRING_LENGTH, 48, 50, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "Press 1", AUTO_STRING_LENGTH, 48, 70, OPAQUE_TEXT);
            Graphics_drawStringCentered(&g_sContext, "to return", AUTO_STRING_LENGTH, 48, 80, OPAQUE_TEXT);
            Graphics_flushBuffer(&g_sContext);

            while(1){
                freq = potVal() + 20;
                timerA2freq(freq);

                while(up == 1){
                    triangleWave = global_time_cnt * 74;
                    DACSetValue(triangleWave);

                    if(triangleWave >= 1480){
                        up = 0;
                        global_time_cnt = 0;
                        countdown = 20;
                        break;
                    }

                    button = press();
                    if(button == 1){
                        break;
                    }

                }

                while(up == 0){
                    triangleWave = countdown * 74;

                    if(triangleWave == 0){
                        up = 1;
                        global_time_cnt = 0;
                        triangleWave = 0;

                    }

                    DACSetValue(triangleWave);

                    button = press();
                    if(button == 1){
                        break;
                    }
                }


                button = press();
                if(button == 1){
                    Graphics_clearDisplay(&g_sContext);
                    state = WELCOME;
                    break;
                }
            }
            break;
        }
    }

}
