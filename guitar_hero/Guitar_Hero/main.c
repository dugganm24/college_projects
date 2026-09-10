
#include <msp430.h>


#include "peripherals.h"
#include <stdlib.h>

//Function Prototypes
void configButton(void);
int press();
void configUserLED();
void UserLEDon(char inbits);
void UserLEDoff(char inbits);
void runtimerA2(void);
void BuzzerOn();
void BuzzerOff();

// Declare globals here
long unsigned int timer = 0;
long unsigned int leap_cnt = 0;

struct song{
    int noteFreq; //note frequency
    int length; //length of note
    char button; //button associated with note
    long int delay; //Delay between current note and next note
    char LED; //LED associated with note
};

void runtimerA2(void){
    TA2CTL = TASSEL_1 + ID_0 + MC_1; //ACLK,Divider=1,Up mode
    TA2CCR0 = 164; //164+1 = 165 ACLK ticks => ~5 ms
    TA2CCTL0 = CCIE; //Interrupt enabled
}

#pragma vector = TIMER2_A0_VECTOR
__interrupt void Timer_A2_ISR(void){
    if(leap_cnt < 1024) {

        timer++;
        leap_cnt++;
    } else {

        timer+=2;
        leap_cnt = 0;
    }
}

// Main
void main(void){

    WDTCTL = WDTPW + WDTHOLD; //Stop watchdog timer

    //Declare Locals
    int state = 0;
    unsigned char currKey = 0;
    int i = 0;
    long unsigned int timer_on = 0;
    int button;
    int correct = 0; //To check if
    int error = 0; //User I/P --> 0=incorrect, 1=correct


    //Call song struct and initialize each note
    struct song note[63];

   //Delay isn't used

    note[0].noteFreq =  293;
    note[0].length = 30;
    note[0].button = 1;
    note[0].delay = 0;
    note[0].LED = BIT3;

    note[1].noteFreq =  293;
    note[1].length = 30;
    note[1].button = 1;
    note[1].delay = 0;
    note[1].LED = BIT3;

    note[2].noteFreq = 293;
    note[2].length = 30;
    note[2].button = 1;
    note[2].delay = 0;
    note[2].LED = BIT3;

    note[3].noteFreq = 392;
    note[3].length = 200;
    note[3].button = 1;
    note[3].delay = 0;
    note[3].LED = BIT2;

    note[4].noteFreq =  587;
    note[4].length = 200;
    note[4].button = 4;
    note[4].delay = 0;
    note[4].LED = BIT0;

    note[5].noteFreq =  523;
    note[5].length = 30;
    note[5].button = 4;
    note[5].delay = 0;
    note[5].LED = BIT0;

    note[6].noteFreq =  493;
    note[6].length = 30;
    note[6].button = 3;
    note[6].delay = 0;
    note[6].LED = BIT0;

    note[7].noteFreq =  440;
    note[7].length = 30;
    note[7].button = 3;
    note[7].delay = 0;
    note[7].LED = BIT1;

    note[8].noteFreq =  783;
    note[8].length = 200;
    note[8].button = 4;
    note[8].delay = 0;
    note[8].LED = BIT0;

    note[9].noteFreq =  587;
    note[9].length = 200;
    note[9].button = 4;
    note[9].delay = 0;
    note[9].LED = BIT0;

    note[10].noteFreq =  523;
    note[10].length = 30;
    note[10].button = 4;
    note[10].delay = 0;
    note[10].LED = BIT0;

    note[11].noteFreq =  493;
    note[11].length = 30;
    note[11].button = 3;
    note[11].delay = 0;
    note[11].LED = BIT1;

    note[12].noteFreq =  440;
    note[12].length = 30;
    note[12].button = 3;
    note[12].delay = 0;
    note[12].LED = BIT1;

    note[13].noteFreq =  783;
    note[13].length = 200;
    note[13].button = 4;
    note[13].delay = 0;
    note[13].LED = BIT0;

    note[14].noteFreq =  587;
    note[14].length = 200;
    note[14].button = 4;
    note[14].delay = 0;
    note[14].LED = BIT0;

    note[15].noteFreq =  523;
    note[15].length = 30;
    note[15].button = 4;
    note[15].delay = 0;
    note[15].LED = BIT0;

    note[16].noteFreq =  493;
    note[16].length = 30;
    note[16].button = 3;
    note[16].delay = 0;
    note[16].LED = BIT1;

    note[17].noteFreq =  523;
    note[17].length = 30;
    note[17].button = 4;
    note[17].delay = 0;
    note[17].LED = BIT0;

    note[18].noteFreq =  440;
    note[18].length = 200;
    note[18].button = 3;
    note[18].delay = 0;
    note[18].LED = BIT1;

    note[19].noteFreq =  293;
    note[19].length = 100;
    note[19].button = 1;
    note[19].delay = 0;
    note[19].LED = BIT3;

    note[20].noteFreq = 293;
    note[20].length = 100;
    note[20].button = 1;
    note[20].delay = 0;
    note[20].LED = BIT3;

    note[21].noteFreq = 392;
    note[21].length = 200;
    note[21].button = 2;
    note[21].delay = 0;
    note[21].LED = BIT2;

    note[22].noteFreq =  587;
    note[22].length = 200;
    note[22].button = 4;
    note[22].delay = 0;
    note[22].LED = BIT0;

    note[23].noteFreq =  523;
    note[23].length = 30;
    note[23].button = 4;
    note[23].delay = 0;
    note[23].LED = BIT0;

    note[24].noteFreq =  493;
    note[24].length = 30;
    note[24].button = 3;
    note[24].delay = 0;
    note[24].LED = BIT1;

    note[25].noteFreq =  440;
    note[25].length = 30;
    note[25].button = 3;
    note[25].delay = 0;
    note[25].LED = BIT1;

    note[26].noteFreq =  783;
    note[26].length = 200;
    note[26].button = 4;
    note[26].delay = 0;
    note[26].LED = BIT0;

    note[27].noteFreq =  587;
    note[27].length = 200;
    note[27].button = 4;
    note[27].delay = 0;
    note[27].LED = BIT0;

    note[28].noteFreq =  523;
    note[28].length = 30;
    note[28].button = 4;
    note[28].delay = 0;
    note[28].LED = BIT0;

    note[29].noteFreq =  493;
    note[29].length = 30;
    note[29].button = 3;
    note[29].delay = 0;
    note[29].LED = BIT1;

    note[30].noteFreq =  440;
    note[30].length = 30;
    note[30].button = 3;
    note[30].delay = 0;
    note[30].LED = BIT1;

    note[31].noteFreq =  783;
    note[31].length = 200;
    note[31].button = 4;
    note[31].delay = 0;
    note[31].LED = BIT0;

    note[32].noteFreq =  587;
    note[32].length = 200;
    note[32].button = 4;
    note[32].delay = 0;
    note[32].LED = BIT0;

    note[33].noteFreq =  523;
    note[33].length = 30;
    note[33].button = 4;
    note[33].delay = 0;
    note[33].LED = BIT0;

    note[34].noteFreq =  493;
    note[34].length = 30;
    note[34].button = 3;
    note[34].delay = 0;
    note[34].LED = BIT1;

    note[35].noteFreq =  523;
    note[35].length = 30;
    note[35].button = 4;
    note[35].delay = 0;
    note[35].LED = BIT4;

    note[36].noteFreq =  440;
    note[36].length = 200;
    note[36].button = 3;
    note[36].delay = 0;
    note[36].LED = BIT1;

    note[37].noteFreq =  293;
    note[37].length = 100;
    note[37].button = 1;
    note[37].delay = 0;
    note[37].LED = BIT3;

    note[38].noteFreq =  293;
    note[38].length = 100;
    note[38].button = 1;
    note[38].delay = 0;
    note[38].LED = BIT3;

    note[39].noteFreq =  329;
    note[39].length = 200;
    note[39].button = 1;
    note[39].delay = 0;
    note[39].LED = BIT3;

    note[40].noteFreq =  329;
    note[40].length = 100;
    note[40].button = 1;
    note[40].delay = 0;
    note[40].LED = BIT3;

    note[41].noteFreq =  523;
    note[41].length = 25;
    note[41].button = 4;
    note[41].delay = 0;
    note[41].LED = BIT0;

    note[42].noteFreq =  493;
    note[42].length = 25;
    note[42].button = 3;
    note[42].delay = 0;
    note[42].LED = BIT1;

    note[43].noteFreq =  440;
    note[43].length = 25;
    note[43].button = 3;
    note[43].delay = 0;
    note[43].LED = BIT1;

    note[44].noteFreq =  392;
    note[44].length = 25;
    note[44].button = 2;
    note[44].delay = 0;
    note[44].LED = BIT2;

    note[45].noteFreq =  392;
    note[45].length = 30;
    note[45].button = 2;
    note[45].delay = 0;
    note[45].LED = BIT2;

    note[46].noteFreq =  440;
    note[46].length = 30;
    note[46].button = 3;
    note[46].delay = 0;
    note[46].LED = BIT1;

    note[47].noteFreq =  493;
    note[47].length = 30;
    note[47].button = 3;
    note[47].delay = 0;
    note[47].LED = BIT1;

    note[48].noteFreq =  440;
    note[48].length = 100;
    note[48].button = 3;
    note[48].delay = 0;
    note[48].LED = BIT1;

    note[49].noteFreq =  329;
    note[49].length = 100;
    note[49].button = 1;
    note[49].delay = 0;
    note[49].LED = BIT3;

    note[50].noteFreq =  369;
    note[50].length = 100;
    note[50].button = 2;
    note[50].delay = 0;
    note[50].LED = BIT2;

    note[51].noteFreq =  293;
    note[51].length = 200;
    note[51].button = 1;
    note[51].delay = 0;
    note[51].LED = BIT3;

    note[52].noteFreq =  293;
    note[52].length = 200;
    note[52].button = 1;
    note[52].delay = 0;
    note[52].LED = BIT3;

    note[53].noteFreq =  329;
    note[53].length = 100;
    note[53].button = 1;
    note[53].delay = 0;
    note[53].LED = BIT3;

    note[54].noteFreq =  329;
    note[54].length = 100;
    note[54].button = 1;
    note[54].delay = 0;
    note[54].LED = BIT3;

    note[55].noteFreq =  523;
    note[55].length = 25;
    note[55].button = 4;
    note[55].delay = 0;
    note[55].LED = BIT0;

    note[56].noteFreq =  493;
    note[56].length = 25;
    note[56].button = 3;
    note[56].delay = 0;
    note[56].LED = BIT1;

    note[57].noteFreq =  440;
    note[57].length = 25;
    note[57].button = 3;
    note[57].delay = 0;
    note[57].LED = BIT1;

    note[58].noteFreq =  392;
    note[58].length = 25;
    note[58].button = 2;
    note[58].delay = 0;
    note[58].LED = BIT2;

    note[59].noteFreq =  587;
    note[59].length = 100;
    note[59].button = 4;
    note[59].delay = 0;
    note[59].LED = BIT0;

    note[60].noteFreq =  440;
    note[60].length = 200;
    note[60].button = 3;
    note[60].delay = 0;
    note[60].LED = BIT1;

    note[61].noteFreq =  293;
    note[61].length = 100;
    note[61].button = 1;
    note[61].delay = 0;
    note[61].LED = BIT3;

    note[62].noteFreq =  293;
    note[62].length = 100;
    note[62].button = 1;
    note[62].delay = 0;
    note[62].LED = BIT3;

    initLeds(); //Initialize LEDs
    configUserLED(); //Config Launchpad LEDs
    configButton(); //Config S1-S4
    configKeypad();
    configDisplay();

    _BIS_SR(GIE); //Global Interrupt Enable
    runtimerA2();

    while(1){
        switch(state){
        case 0:

            //Starting screen & countdown to start of game

            Graphics_drawStringCentered(&g_sContext, "MSP430 Hero", AUTO_STRING_LENGTH, 48, 35, TRANSPARENT_TEXT); // Display welcome screen
            Graphics_drawStringCentered(&g_sContext, "Press * to Start", AUTO_STRING_LENGTH, 48, 55, TRANSPARENT_TEXT); // Display welcome screen
            Graphics_flushBuffer(&g_sContext);

            currKey = getKey();
            if(currKey == '*'){
                timer_on = timer;
                Graphics_clearDisplay(&g_sContext);
                Graphics_flushBuffer(&g_sContext);
                while(timer < timer_on + 200){
                    Graphics_drawStringCentered(&g_sContext, "3", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_flushBuffer(&g_sContext);
                    UserLEDon(1);
                }
                Graphics_clearDisplay(&g_sContext);
                Graphics_flushBuffer(&g_sContext);
                UserLEDoff(1);

                timer_on = timer;
                while(timer < timer_on + 200){
                    Graphics_drawStringCentered(&g_sContext, "2", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_flushBuffer(&g_sContext);
                    UserLEDon(2);
                }
                Graphics_clearDisplay(&g_sContext);
                Graphics_flushBuffer(&g_sContext);
                UserLEDoff(2);

                timer_on = timer;
                while(timer < timer_on + 200){
                    Graphics_drawStringCentered(&g_sContext, "1", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_flushBuffer(&g_sContext);
                    UserLEDon(1);
                }
                Graphics_clearDisplay(&g_sContext);
                Graphics_flushBuffer(&g_sContext);
                UserLEDoff(1);

                timer_on = timer;
                while(timer < timer_on + 200){
                    Graphics_drawStringCentered(&g_sContext, "GO!", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_flushBuffer(&g_sContext);
                    UserLEDon(3);
                }
                Graphics_clearDisplay(&g_sContext);
                Graphics_flushBuffer(&g_sContext);
                UserLEDoff(3);
                state = 1;

            }
            break;

        case 1:
            for(i=0; i<63; i++){
                correct = 0; //Resets correct to assume false unless correct I/P
                currKey = getKey();
                if(currKey == '#'){ //Press # to reset game
                    state = 0;
                    i = 0;
                    currKey = 0;
                    Graphics_clearDisplay(&g_sContext);
                    break;
                }

                timer_on = timer;
                while(timer <(timer_on + note[i].length + 50)){ //Runs while timer < length of note
                    BuzzerOn(note[i].noteFreq); //Plays current note frequency
                    setLeds(note[i].LED); //Displays current note LED
                    button = press(); //Sets button = S1-S4 press

                    if(button == note[i].button){ //Checks if button pressed = button associated w/ note
                        correct = 1; //It is correct --> 1=True, 0=False
                    }

                }

                BuzzerOff();
                setLeds(0);

                timer_on = timer;
                while(timer < (timer_on+50)){ // 1/4 second pause

                }

                if(correct != 1){ //If correct not equal 1 (true)
                    error += 1; //Increment error
                }

                if(error == 7){ //Once we reac 7 errors, we lose
                    Graphics_drawStringCentered(&g_sContext, "Game Over!", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_flushBuffer(&g_sContext);
                    //Player embarrassment
                    setLeds(15);
                    BuzzerOn(100);

                    timer_on = timer;
                    while(timer < (timer_on + 600)){ //3 sec pause

                    }

                    setLeds(0);
                    BuzzerOff();

                    Graphics_clearDisplay(&g_sContext); //Reset everything
                    i = 0;
                    error = 0;
                    state = 0;
                    break;
                }

                if(i == 63){ //If note = 63 (greater than total # notes) display you win
                    Graphics_drawStringCentered(&g_sContext, "You Win!", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_flushBuffer(&g_sContext);
                    //Player celebration
                    setLeds(15);

                    timer_on = timer;
                    while(timer < (timer_on + 600)){ //3 sec pause

                    }

                    setLeds(0);
                    Graphics_clearDisplay(&g_sContext);
                    i = 0;
                    error = 0;
                    state = 0;
                    break;
                }
            }
        }
    }
}







