import { Model } from './model'
import { Coordinate } from './model'

export function swapController(model:Model) : Model{
  if(model.board.selected.length === 2){
    model.swap(model.board.selected)
    return model
  }
  return model 
}

export function undoController(model:Model) : Model{
  if(model.swaps.length > 0){
    model.undo()
  }
  return model 
}

export function resetController(model:Model) : Model{
  const newModel = new Model(model.configuration)
  return newModel
}

export function configController(model:Model, configuration:any) : Model{
  const newModel = new Model(configuration)
  return newModel
}

export function selectController(model:Model, location:Coordinate): Model{
  const selected = model.board.selected
  const index = selected.findIndex((coordinate) => coordinate.row === location.row && coordinate.column === location.column)
  if(index === -1){
    if(selected.length >= 2){
      selected.shift()
    }
    selected.push(location)
  } 
  else{
    selected.splice(index, 1)
  }
  return model 
}
  