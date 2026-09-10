import { expect, test } from 'vitest'
import { Model } from './model'
import { resetController, configController, swapController, undoController, selectController } from './controllers'
import { config1, config2 } from './puzzle'

const mockModel = new Model(config1);

const mockCoordinate1 = { row: 0, column: 0 }
const mockCoordinate2 = { row: 1, column: 1 }
const mockCoordinate3 = { row: 2, column: 2 }

test('ModelChanges', () => {
    let newModel = resetController(mockModel)
    expect(newModel).not.toBe(mockModel)
    expect(newModel.configuration).toEqual(mockModel.configuration)

    newModel = configController(mockModel, config2)
    expect(newModel.configuration).toEqual(config2)


    mockModel.board.selected = [mockCoordinate1, mockCoordinate2]
    newModel = swapController(mockModel)
    expect(newModel.board.selected).toHaveLength(0)
    expect(newModel.numSwaps).toBe(1)

    newModel = undoController(mockModel)
    expect(newModel.swaps).toHaveLength(0)

    newModel = selectController(mockModel, mockCoordinate1)
    newModel = selectController(mockModel, mockCoordinate2)
    expect(newModel.board.selected).toEqual([mockCoordinate1, mockCoordinate2])

    newModel = selectController(mockModel, mockCoordinate3)
    expect(newModel.board.selected).toEqual([mockCoordinate2, mockCoordinate3])

    newModel = selectController(mockModel, mockCoordinate2)
    expect(newModel.board.selected).toEqual([mockCoordinate3])

});