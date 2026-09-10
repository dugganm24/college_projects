
#include <msp430.h>


#include "peripherals.h"
#include <stdlib.h>

#define CALADC12_15V_30C *((unsigned int *)0x1A1A)
#define CALADC12_15V_85C *((unsigned int *)0x1A1C)

//Function Prototypes


// Declare globals here
long unsigned int global_time_cnt;
unsigned int in_temp;
//unsigned int in_value;
long unsigned int days;
long unsigned int hr;
long unsigned int min;
long unsigned int sec;

enum State{DISPLAY, DELAY, EDIT};
enum DisplayState{DATE, TIME, TEMP_C, TEMP_F};
enum TimeUnit{MONTH, DAY, HOUR, MIN, SEC};

void runTimerA2(void){
    TA2CTL = TASSEL_1 + ID_0 + MC_1; //ACLK, Divider=1, Up mode
    TA2CCR0 = 32767; //32767 tics +1 = 1s exact (no leap cnt)
    TA2CCTL0 = CCIE; //Interrupt enabled
}

void stopTimerA2(void){
    TA2CCTL0 = 0;
}

void startTimerA2(void){
    TA2CCTL0 = CCIE;
}

#pragma vector = TIMER2_A0_VECTOR
__interrupt void Timer_A2_ISR(void){
    global_time_cnt++;
}

void displayDate(long unsigned int inTime){
    char date[] = {'M', 'M', 'M', ' ', 'D', 'D', '\0'};
    // long unsigned int days;

    days = inTime/(86400) + 1;

    if(days <=31){
        date[0] = 'J';
        date[1] = 'A';
        date[2] = 'N';
    }
    if(31 < days && days <= 59){
        date[0] = 'F';
        date[1] = 'E';
        date[2] = 'B';
        days -= 31;
    }
    if(59 < days && days <=90){
        date[0] = 'M';
        date[1] = 'A';
        date[2] = 'R';
        days -= 59;
    }
    if(90 < days && days <= 120){
        date[0] = 'A';
        date[1] = 'P';
        date[2] = 'R';
        days -= 90;
    }
    if(120 < days && days <= 151){
        date[0] = 'M';
        date[1] = 'A';
        date[2] = 'Y';
        days -= 120;
    }
    if(151 < days && days <=181){
        date[0] = 'J';
        date[1] = 'U';
        date[2] = 'N';
        days -= 151;
    }
    if(181 < days && days <= 212){
        date[0] = 'J';
        date[1] = 'U';
        date[2] = 'L';
        days -= 181;
    }
    if(212 < days && days <=243){
        date[0] = 'A';
        date[1] = 'U';
        date[2] = 'G';
        days -= 212;
    }
    if(243 < days && days <=273){
        date[0] = 'S';
        date[1] = 'E';
        date[2] = 'P';
        days -= 243;
    }
    if(273 < days && days <= 304){
        date[0] = 'O';
        date[1] = 'C';
        date[2] = 'T';
        days -= 273;
    }
    if(304 < days && days <= 334){
        date[0] = 'N';
        date[1] = 'O';
        date[2] = 'V';
        days -= 304;
    }
    if(334 < days && days <=365){
        date[0] = 'D';
        date[1] = 'E';
        date[2] = 'C';
        days -= 334;
    }

    date[4] = days/10 + 0x30;
    date[5] = days%10 + 0x30;

    Graphics_drawStringCentered(&g_sContext, date, AUTO_STRING_LENGTH, 48, 32, TRANSPARENT_TEXT);
    Graphics_flushBuffer(&g_sContext);
}

void displayTime(long unsigned int inTime){
    char time[] = {'H', 'H', ':', 'M', 'M', ':', 'S', 'S', '\0'};
    long unsigned int hr_loc;
    long unsigned int min_loc;
    long unsigned int sec_loc;
    long unsigned int day_loc;
    long unsigned int remainder = 0;

    day_loc = inTime/86400 + 1;
    remainder = inTime - (day_loc - 1)*86400;

    hr_loc = remainder/3600;
    remainder = remainder - hr_loc*3600;
    min_loc = remainder/60;
    sec_loc = remainder - min_loc*60;

    time[0] = hr_loc/10 + 0x30;
    time[1] = hr_loc%10 + 0x30;
    time[3] = min_loc/10 + 0x30;
    time[4] = min_loc%10 + 0x30;
    time[6] = sec_loc/10 + 0x30;
    time[7] = sec_loc%10 + 0x30;

    Graphics_drawStringCentered(&g_sContext, time, AUTO_STRING_LENGTH, 48, 40, TRANSPARENT_TEXT);
    Graphics_flushBuffer(&g_sContext);
}


void displayTempC(float inAvgTempC){

    char tempC[] = {'d', 'd', 'd', '.', 'f', 'C', '\0'};

    tempC[0] = (int)inAvgTempC/100 + 0x30;
    tempC[1] = (int)inAvgTempC/10 % 10 + 0x30;
    tempC[2] = (int)inAvgTempC % 10 + 0x30;
    tempC[4] = (int)(inAvgTempC * 10) % 10 + 0x30;

    Graphics_drawStringCentered(&g_sContext, tempC, AUTO_STRING_LENGTH, 48, 56, TRANSPARENT_TEXT);
    Graphics_flushBuffer(&g_sContext);
}

void displayTempF(float inAvgTempC){
    float inAvgTempF = inAvgTempC*(9/5) + 32;
    char tempF[] = {'d', 'd', 'd', '.', 'f', 'F', '\0'};

    tempF[0] = (int)inAvgTempF/100 + 0x30;
    tempF[1] = (int)inAvgTempF/10 % 10 + 0x30;
    tempF[2] =(int)inAvgTempF % 10 + 0x30;
    tempF[4] = (int)(inAvgTempF * 10) % 10 + 0x30;

    Graphics_drawStringCentered(&g_sContext, tempF, AUTO_STRING_LENGTH, 48, 64, TRANSPARENT_TEXT);
    Graphics_flushBuffer(&g_sContext);
}

void setADC12_temp(void){
    REFCTL0 &= ~REFMSTR;
    ADC12CTL0 = ADC12SHT0_9 + ADC12REFON + ADC12ON;
    ADC12CTL1 = ADC12SHP;
    ADC12MCTL0 = ADC12SREF_1 + ADC12INCH_10;

    __delay_cycles(100);
    ADC12CTL0 |= ADC12ENC;
}

float tempC(){
    volatile float temperatureDegC;
    volatile float temperatureDegF;
    volatile float degC_per_bit;
    volatile unsigned int bits30, bits85;

    ADC12CTL0 &= ~ADC12SC;
    ADC12CTL0 |= ADC12SC;

    while(ADC12CTL1 & ADC12BUSY)
        __no_operation();
    in_temp = ADC12MEM0;

    temperatureDegC = (float)((long)in_temp - CALADC12_15V_30C) * degC_per_bit + 30;
}

float averageArray(float* array){
    int i;
    float average = 0;
    for( i = 0; i < 36; i++){
        average += array[i];
    }
    return average / 36;
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

void main(void){

    WDTCTL = WDTPW | WDTHOLD;
    _BIS_SR(GIE);

    configDisplay();
    configKeypad();
    configButton();
    runTimerA2();
    setADC12_temp();
    setADC12_scroll();

    float avgTemp[36];
    float currTemp;
    int i;
    int button;
    int scrollVal;
    long unsigned int delay;
    long unsigned int startTime;
    enum State state = DISPLAY;
    enum  DisplayState displayState = DATE;
    enum TimeUnit currentUnit = DAY;

    currTemp = tempC();
    for(i = 0; i < 36; i++){
        avgTemp[i] = currTemp;
    }

    while(1){

        switch(state){
        case DISPLAY:

            Graphics_clearDisplay(&g_sContext);
            Graphics_flushBuffer(&g_sContext);

            startTime = global_time_cnt;

            switch(displayState){
            case DATE:
                displayDate(global_time_cnt);
                break;

            case TIME:
                displayTime(global_time_cnt);
                break;

            case TEMP_C:
                currTemp = tempC();
                avgTemp[global_time_cnt % 36] = currTemp;
                displayTempC(averageArray(avgTemp));
                break;

            case TEMP_F:
                currTemp = tempC();
                avgTemp[global_time_cnt % 36] = currTemp;
                displayTempF(averageArray(avgTemp));
                break;
            }
            state = DELAY;
            break;

            case DELAY:

                button = press();
                if(button == 1){
                    stopTimerA2();
                    setADC12_scroll();
                    currentUnit = DAY;
                    state = EDIT;
                }

                delay = global_time_cnt - startTime;

                if(delay >= 3){
                    switch(displayState){
                    case DATE:
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        displayState = TIME;
                        break;

                    case TIME:
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        displayState = TEMP_C;
                        break;

                    case TEMP_C:
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        displayState = TEMP_F;
                        break;

                    case TEMP_F:
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        displayState = DATE;
                        break;
                    }
                    state = DISPLAY;
                }
                break;

            case EDIT:

                Graphics_clearDisplay(&g_sContext);
                Graphics_flushBuffer(&g_sContext);

                button = 0;
                button = press();

                if(button == 2){
                    global_time_cnt = (days + hr + min + sec);
                    startTimerA2();
                    setADC12_temp();
                    state = DISPLAY;
                }
                switch(currentUnit){

                case DAY:

                    scrollVal = potVal();
                    button = press();
                    displayDate(global_time_cnt);

                    days = (scrollVal/(4095/365)) * 86400;
                    global_time_cnt = (days + hr + min + sec);

                    if(button == 1){
                        days = (scrollVal/(4095/365)) * 86400;
                        currentUnit = HOUR;
                        break;
                    }

                    if(button == 2){
                        global_time_cnt = (days + hr + min + sec);
                        startTimerA2();
                        setADC12_temp();
                        state = DISPLAY;
                        break;
                    }

                    else{
                        currentUnit = DAY;
                    }

                  break;

                case HOUR:

                    scrollVal = potVal();
                    button = press();
                    displayTime(global_time_cnt);

                    hr = (scrollVal / (4095/24)) * 3600;
                    global_time_cnt = (days + hr + min + sec);

                    if(button == 1){
                        hr = (scrollVal / (4095/24)) * 3600;
                        currentUnit = MIN;
                        break;
                    }

                    if(button == 2){
                        global_time_cnt = (days + hr + min + sec);
                        startTimerA2();
                        setADC12_temp();
                        state = DISPLAY;
                        break;
                    }

                    else{
                        currentUnit = HOUR;
                    }

                    break;

                case MIN:

                    scrollVal = potVal();
                    button = press();
                    displayTime(global_time_cnt);

                    min = (scrollVal / (4095/60)) * 60;
                    global_time_cnt = (days + hr + min + sec);

                    if(button == 1){
                        min = (scrollVal / (4095/60)) * 60;
                        currentUnit = SEC;
                        break;
                    }

                    if(button == 2){
                        global_time_cnt = (days + hr + min + sec);
                        startTimerA2();
                        setADC12_temp();
                        state = DISPLAY;
                        break;
                    }

                    else{
                        currentUnit = MIN;
                    }

                    break;

                case SEC:

                    scrollVal = potVal();
                    button = press();
                    displayTime(global_time_cnt);

                    sec = (scrollVal/ (4095/60));
                    global_time_cnt = (days + hr + min + sec);

                    if(button == 1){
                        sec = (scrollVal/ (4095/60));
                        currentUnit = DAY;
                        break;
                    }

                    if(button == 2){
                        global_time_cnt = (days + hr + min + sec);
                        startTimerA2();
                        setADC12_temp();
                        state = DISPLAY;
                        break;
                    }

                    else{
                        currentUnit = SEC;
                    }

                    break;

                }

                // Graphics_clearDisplay(&g_sContext);
               // displayTime(days + hr + min + sec);
        }

    }

}



















