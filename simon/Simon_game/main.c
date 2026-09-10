
//#include <msp430.h>

/* Peripherals.c and .h are where the functions that implement
 * the LEDs and keypad, etc are. It is often useful to organize
 * your code by putting like functions together in files.
 * You include the header associated with that file(s)
 * into the main file of your project. */
#include "peripherals.h"
#include <stdlib.h>
 //Function Prototypes
void swDelay(char numLoops);
void swDelay2(char numLoops2);
void configure(void);
char reset(void);
// Declare globals here




// Main

 void main(void)
{
    int currKey=0;
    int state;
    int button[31]; //input
    int sequence[31]; //displayed on LED
    int randnum;
    int level = 0; //tracks current level game is on
    int fourLeds = 15; //binary to display lose lights
    int i;
    int currentInput = 0; //used to track which input is being checked
    int disp;
    int speed = 32;
    int key = 0;
    int press;
    int pressed;
    state = 0;

    WDTCTL = WDTPW | WDTHOLD;    // Stop watchdog timer. 
    // You can then configure it properly, if desired
    // Useful code starts here
    initLeds();
    configDisplay();
    configKeypad();
    while(1)
    {
        currKey = getKey();
        switch(state)
        {
        case 0:
            // *** Intro Screen ***

            // Write some text to the display
            Graphics_drawStringCentered(&g_sContext, "SIMON", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
            // Draw a box around everything because it looks nice
            Graphics_Rectangle box = {.xMin = 5, .xMax = 91, .yMin = 5, .yMax = 91 };
            Graphics_drawRectangle(&g_sContext, &box);
            // We are now done writing to the display.  However, if we stopped here, we would not
            // see any changes on the actual LCD.  This is because we need to send our changes
            // to the LCD, which then refreshes the display.
            // Since this is a slow operation, it is best to refresh (or "flush") only after
            // we are done drawing everything we need.
            Graphics_flushBuffer(&g_sContext);
            if (currKey == '*') //starts the game
            {
                for (i = 0; i < 32; i++){ //create the random sequence for all levels
                    randnum = rand() % 4 + 1; //give randnum between 1 to 4
                    sequence[i] = randnum; //set element of sequence array
                }
                state = 1; //change state to 1 to continue game
            }
            else
            {
                state = 0;
            }
            break;
        case 1:
            // ***Checks if user reached max level. Game over if so***
            if(level == 32)
            {
                Graphics_clearDisplay(&g_sContext);
                Graphics_drawStringCentered(&g_sContext, "You Win!", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                Graphics_drawRectangle(&g_sContext, &box);
                Graphics_flushBuffer(&g_sContext);
                swDelay(5);
                Graphics_clearDisplay(&g_sContext);
                state = 0;
                break;
            }
            else if(level > 0) //in middle of game, signal next level
            {
                Graphics_clearDisplay(&g_sContext);
                Graphics_drawStringCentered(&g_sContext, "Next Level ", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                Graphics_drawRectangle(&g_sContext, &box);
                Graphics_flushBuffer(&g_sContext);
                swDelay(5);
                Graphics_clearDisplay(&g_sContext);
            }
            //*** Count down for start ***
            else if(level == 0)
            {
                Graphics_clearDisplay(&g_sContext);
                Graphics_drawStringCentered(&g_sContext, "3", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                Graphics_drawRectangle(&g_sContext, &box);
                Graphics_flushBuffer(&g_sContext);
                swDelay(2);
                Graphics_clearDisplay(&g_sContext);
                Graphics_drawStringCentered(&g_sContext, "2", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                Graphics_drawRectangle(&g_sContext, &box);
                Graphics_flushBuffer(&g_sContext);
                swDelay(2);
                Graphics_clearDisplay(&g_sContext);
                Graphics_drawStringCentered(&g_sContext, "1", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                Graphics_drawRectangle(&g_sContext, &box);
                Graphics_flushBuffer(&g_sContext);
                swDelay(2);
                Graphics_clearDisplay(&g_sContext);

                state = 2; //begin levels
                break;
            }
        case 2:
            // *** display sequence on LEDS ***

            for (i = 0; i <= level; i++){
                disp = sequence[i];
                if(disp == 1) {
                    disp = 8;}
                else if(disp == 2) {
                    disp = 4;}
                else if(disp == 3) {
                    disp = 2;}
                else if (disp == 4) {
                    disp = 1;}
                setLeds(disp - 0x30);
                if(disp == 8) {
                    Buzzer1();
                }
                else if (disp == 4) {
                    Buzzer2();
                }
                else if (disp == 2) {
                    Buzzer3();
                }
                else if (disp == 1) {
                    Buzzer4();
                }
                swDelay2(speed);
                BuzzerOff();
                setLeds(0);
                swDelay(1);
            }
            state = 3; //change state to 3 to check user input
            break;
        case 3:
            // *** Player input and check ***
            for(i=0; i<=level;) //loop through the number of levels to check all the input

            //Loop getKey()
            for (key = 0; key<=level; key++) { //loops through each key to get as many input keys as there is levels
                {
                    configure();
                    press = reset();

                    if(press == 1){
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_drawStringCentered(&g_sContext, "RESTART", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                        Graphics_drawRectangle(&g_sContext, &box);
                        Graphics_flushBuffer(&g_sContext);
                        swDelay(5);
                        Graphics_clearDisplay(&g_sContext);
                        level = 0;
                        button[0] = 5;
                        currentInput = 0;
                        i = 1;
                        pressed = 1;
                        state = 0;
                        break;
                    }

                    currKey = getKey();  //get a single input and store in current key
                    //button[key] = currKey; //store the current key in button array which tracks all the inputs per level

                    if((currKey >='1') && (currKey<='4')) //if the input is valid (1-4)
                    {
                        button[i] = currKey; //store the current input key in the button array

                    }

                    if((currKey >= '5') || (currKey == '0') || (currKey =='*') || (currKey == '#')) //if the input is invalid
                    {
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_drawStringCentered(&g_sContext, "Invalid Entry", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                        Graphics_drawRectangle(&g_sContext, &box);
                        Graphics_flushBuffer(&g_sContext);
                        swDelay(5);
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                    }


                    if(currKey == '1')
                    {
                        button[i] = 1;
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_drawStringCentered(&g_sContext, "1", AUTO_STRING_LENGTH, 20, 48, TRANSPARENT_TEXT);
                        Graphics_drawRectangle(&g_sContext, &box);
                        Graphics_flushBuffer(&g_sContext);
                        swDelay(1);
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        i++;
                    }
                    else if(currKey == '2')
                    {
                        button[i] = 2;
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_drawStringCentered(&g_sContext, "2", AUTO_STRING_LENGTH, 30, 48, TRANSPARENT_TEXT);
                        Graphics_drawRectangle(&g_sContext, &box);
                        Graphics_flushBuffer(&g_sContext);
                        swDelay(1);
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        i++;
                    }
                    else if(currKey == '3')
                    {
                        button[i] = 3;
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_drawStringCentered(&g_sContext, "3", AUTO_STRING_LENGTH, 60, 48, TRANSPARENT_TEXT);
                        Graphics_drawRectangle(&g_sContext, &box);
                        Graphics_flushBuffer(&g_sContext);
                        swDelay(1);
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        i++;
                    }
                    else if (currKey == '4')
                    {
                        button[i] = 4;
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_drawStringCentered(&g_sContext, "4", AUTO_STRING_LENGTH, 70, 48, TRANSPARENT_TEXT);
                        Graphics_drawRectangle(&g_sContext, &box);
                        Graphics_flushBuffer(&g_sContext);
                        swDelay(1);
                        Graphics_clearDisplay(&g_sContext);
                        Graphics_flushBuffer(&g_sContext);
                        i++;
                    }


                }

            }


            for(currentInput=0; currentInput<=level;)
            {


                if(button[currentInput] == sequence[currentInput]) //check if this input is correct
                {
                    currentInput++;
                    //check the next input
                    if(currentInput >= level) //have checked through all inputs
                    {
                        speed--;
                        level++;
                        state = 1;
                        break;
                    }

                }

                else if(button[currentInput] != sequence[currentInput])
                {
                    Graphics_clearDisplay(&g_sContext);
                    Graphics_drawStringCentered(&g_sContext, "GAME OVER", AUTO_STRING_LENGTH, 48, 48, TRANSPARENT_TEXT);
                    Graphics_drawRectangle(&g_sContext, &box);
                    Graphics_flushBuffer(&g_sContext);
                    BuzzerOn();
                    setLeds(fourLeds - 0x30);
                    swDelay(5);
                    BuzzerOff();
                    setLeds(0);
                    Graphics_clearDisplay(&g_sContext);
                    level = 0;
                    currentInput = 0;
                    state = 0;
                    break;
                }
            }

            break;
        }
    }
}

void swDelay(char numLoops)
{
  
    // Input: numLoops = number of delay loops to execute
    // Output: none
    volatile unsigned int i,j;  // volatile to prevent removal in optimization
    // by compiler. Functionally this is useless code
    for (j=0; j<numLoops; j++)
    {
        i = 50000 ;                 // SW Delay
        while (i > 0)               // could also have used while (i)
            i--;
    }
}
void swDelay2(char numLoops2) // swDelay2 lowers the time a led is on to make it more difficult
{
    // Input: numLoops = number of delay loops to execute
    // Output: none
    volatile unsigned int i,j;  // volatile to prevent removal in optimization
    // by compiler. Functionally this is useless code
    for (j=0; j<numLoops2; j++)
    {
        i = 3125;                 // SW Delay
        while (i > 0)               // could also have used while (i)
            i--;
    }
}





