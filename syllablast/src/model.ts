export class Coordinate {
    readonly row : number;
    readonly column : number;

    constructor(row:number, column:number) {
      this.row = row;
      this.column = column;
    }
}

export class Board {
    syllables : string[][]
    selected : Coordinate[]

    constructor() {
        this.syllables = []
        for (let r:number = 0; r < 4; r++) {
            this.syllables[r] = []
            for (let c:number = 0; c < 4; c++) {
                this.syllables[r][c] = ''
            }
        }
        this.selected = []
    }

    initialize(input : string[][]) : void {
        for (let r:number = 0; r < 4; r++) {
            for (let c:number = 0; c < 4; c++) {
                this.syllables[r][c] = input[r][c]
            }
        }
    }

    swap() : boolean {
        if (this.selected.length === 2) {

            let selectedCoor1 = this.selected[0]
            let selectedCoor2 = this.selected[1]
             
            let temp = this.syllables[selectedCoor1.row][selectedCoor1.column]
            this.syllables[selectedCoor1.row][selectedCoor1.column] = this.syllables[selectedCoor2.row][selectedCoor2.column]
            this.syllables[selectedCoor2.row][selectedCoor2.column] = temp


            this.selected = []
            return true 
    }
    return false 
}

    undo(lastSwap : Coordinate[]) : boolean {
        let temp = this.syllables[lastSwap[0].row][lastSwap[0].column]
        this.syllables[lastSwap[0].row][lastSwap[0].column] = this.syllables[lastSwap[1].row][lastSwap[1].column]
        this.syllables[lastSwap[1].row][lastSwap[1].column] = temp
        this.selected = []
        return true
    }

}

export class Model {
    board : Board
    numSwaps : number
    score : number
    victory : boolean
    configuration : any
    words : string[]
    swaps : Coordinate[][]

    constructor(puzzle : any) {
        let board = new Board()
        for (let r:number = 0; r < 4; r++) {
            for (let c:number = 0; c < 4; c++) {
                board.syllables[r][c] = puzzle.initial[r][c]
            }
        }
        this.board = board
        this.numSwaps = 0
        this.score = 0
        this.victory = false
        this.configuration = puzzle
        this.words = puzzle.words
        this.swaps = []
    }

    swap(swapSyllable:Coordinate[]) : boolean{
 
        if(this.hasWon()){
            return false
        }
        
        this.swaps.push(swapSyllable) //add swap to list of swaps 
        this.board.swap() //swap the syllables
        this.calculateScore() //recalculate score
        this.swapCounter(true) //update swap counter 

        return true 
    }

    undo() : Coordinate | boolean{

        if(this.hasWon()){
            return false
        }
    

        let lastSwap = this.swaps.pop()
        //if there is a last swap
        if(lastSwap){
            this.board.undo(lastSwap) //undo the last swap
        }
        this.calculateScore() //recalculate score 
        this.swapCounter(false) //update swap counter
        return true
    }

    calculateScore() : number{

        let score = 0
        const goalWords = this.words.map((word: string) => word.split(','))
    

        //loop through each row of board 
        for(let i = 0; i < 4; i++){
            const row = this.board.syllables[i]
            console.log("Row : "+ row[0])
            console.log("Goal : " +goalWords[0])

            //get the goal word that matches the first syllable of the row
            const goalWordRow = goalWords.find((word) => word[0] === row[0])
            if(goalWordRow){
                let matches = 0
                for(let j = 0; j < 4; j++){
                    if(row[j] === goalWordRow[j]){
                        matches++
                    }
                    else{
                        break
                    }
                }
                score += matches
            }
            else{
                continue
            }
        }
        this.score = score
        this.hasWon()
        return this.score
    }

    

    swapCounter(increment: boolean) : boolean{
        if(increment){
            this.numSwaps++;
        }
        else{
            this.numSwaps--;
        }
        return true
    }

    hasWon() : boolean{
        if(this.score === 16){
            this.victory = true
            return true
        }
        return false
    }

}