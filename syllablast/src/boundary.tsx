'use client'  
import React from 'react'
import { Coordinate } from './model'

interface BoardGUIProps {
  topmodel: any
  redraw: Function
}

export function BoardGUI({ topmodel, redraw }: BoardGUIProps) {

  function handleClick(r: number, c: number) {
    let board = topmodel.board
    if (board.selected.length === 2) {
      board.selected.shift()
    }
    board.selected.push(new Coordinate(r, c))
    redraw() 
  }

  function isSelected(r:number, c:number) : boolean {
    return topmodel.board.selected.some((coordinate: Coordinate) => coordinate.row === r && coordinate.column === c) 
  }

  function correctPosition(coord: Coordinate): boolean {
    const { row, column } = coord;
    const currentSyllable = topmodel.board.syllables[row][column];
    let words = topmodel.words.map((word: string) => word.split(','));;

    for (const targetWord of words) {
      if (targetWord[column] === currentSyllable) {
        let allPreviousCorrect = true;
        for (let c = 0; c < column; c++) {
          if (targetWord[c] !== topmodel.board.syllables[row][c]) {
            allPreviousCorrect = false;
            break;
          }
        }
        if (allPreviousCorrect) {
          return true;
        }
      }
    }

    return false;
  }

  return (
    <div className="grid grid-cols-4 gap-2">
      {topmodel.board.syllables.map((row: string[], rIdx: number) => (
        row.map((syllable: string, cIdx: number) => (
          <button
            key={`${rIdx}-${cIdx}`}
            className={
              (isSelected(rIdx, cIdx) ? 'selected-square' : '') +
              (correctPosition(new Coordinate(rIdx, cIdx)) ? ' correct-square' : '') +
              ' square'
            }
            onClick={() => handleClick(rIdx, cIdx)}
          >
            {syllable}
          </button>
        ))
      ))}
    </div>
  )
}