import { expect, test } from 'vitest'
import { Coordinate, Board, Model } from './model'
import { config1 } from './puzzle'
import { config, exitCode } from 'process'

test('Coordinate', () => {
    let c1 = new Coordinate(3, 2)
    expect(c1.row).toBe(3)
    expect(c1.column).toBe(2)
})

test('Board', () => {
    let b = new Board()
    expect(b.syllables).toBeDefined()
    expect(b.selected).toBeDefined()

    b.initialize(config1.initial)   

    expect(b.syllables[0][0]).toBe('ter')
    expect(b.syllables[3][3]).toBe('wa')

    let c1 = new Coordinate(0, 0)
    let c2 = new Coordinate(0, 1)
    b.selected = [c1, c2]
    b.swap()
    expect(b.syllables[0][0]).toBe('ate')
    expect(b.syllables[0][1]).toBe('ter')
    b.undo([c1, c2])
    expect(b.syllables[0][0]).toBe('ter')
    expect(b.syllables[0][1]).toBe('ate')
})

test('Model', () => {
    let m = new Model(config1)
    expect(m.words).toBeDefined()
    expect(m.board).toBeDefined()
    expect(m.numSwaps).toBe(0)
    expect(m.score).toBe(0)
    expect(m.victory).toBe(false)
    expect(m.swaps).toBeDefined()
    expect(m.configuration).toBeDefined()
    expect(m.hasWon()).toBe(false)
    expect(m.calculateScore()).toBe(0)

    m.swapCounter(true)
    expect(m.numSwaps).toBe(1)
    m.swapCounter(false)
    expect(m.numSwaps).toBe(0)

    m.swap([new Coordinate(0, 0), new Coordinate(0, 1)])
    expect(m.board.syllables[0][0]).toBe('ter')
    m.undo()
    expect(m.board.syllables[0][0]).toBe('ate')

    m.board.syllables = m.words.map((word: string) => word.split(','))
    m.calculateScore()
    expect(m.score).toBe(16)
    expect(m.hasWon()).toBe(true)
})
