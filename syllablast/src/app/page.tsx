'use client'  // directive to clarify client-side
import React, { useState } from 'react'

import { config1, config2, config3 } from '../puzzle'
import { Model } from '../model'
import { BoardGUI } from '../boundary'
import { resetController, selectController, configController, swapController, undoController } from '../controllers'

var actualPuzzle = config1

export default function Home() {
  const [model, setModel] = React.useState(new Model(actualPuzzle))
  const [redraw, setRedraw] = React.useState(0)

  function refresh() {
    setRedraw(redraw + 1)
  }

  function handleReset() {
    const newModel = resetController(model)
    setModel(newModel)
    refresh()
  }

  function handleSwap(){
    const newModel = swapController(model)
    setModel(newModel)
    refresh()
  }

  function handleUndo() {
    const newModel = undoController(model)
    setModel(newModel)
    refresh()
  }

  function handleConfigSelect(config : any) {
    const newModel = configController(model, config)
    setModel(newModel)
    refresh()
  }

  
  return (
    <div className="min-h-screen p-8 sm:p-20 font-[family-name:var(--font-geist-sans)] grid grid-cols-3 gap-20"> 
      <div className="flex flex-col items-center">
        <div className="flex justify-center mb-4">
          <button className="bg-gray-300 p-4 mx-2 rounded" onClick={() => handleConfigSelect(config1)}>Game 1</button>
          <button className="bg-gray-300 p-4 mx-2 rounded" onClick={() => handleConfigSelect(config2)}>Game 2</button>
          <button className="bg-gray-300 p-4 mx-2 rounded" onClick={() => handleConfigSelect(config3)}>Game 3</button>
        </div>
        <BoardGUI topmodel={model} redraw={refresh} />
        <div className="mt-4 space-x-4 flex justify-center">
          <button className="bg-gray-300 p-4 rounded" onClick={() => handleSwap()}>Swap</button>
          <button className="bg-gray-300 p-4 rounded" onClick={() => handleUndo()}>Undo</button>
          <button className="bg-gray-300 p-4 rounded" onClick={() => handleReset()}>Reset</button>
        </div>
      </div>
      <div className="flex flex-col justify-center items-start space-y-4">
        <p className="text-lg font-bold">Swaps: {model.numSwaps}</p>
        <p className="text-lg font-bold">Score: {model.score}</p>
        <p className="text-lg font-bold">{model.hasWon() && (
        <div
          data-testid="victory-label"
          style={{
            color: 'green',
            fontSize: '24px',
            marginLeft: '120px',
          }}
        >
          Game Complete
        </div>
      )} </p>
      </div>
    </div>
  )
}
