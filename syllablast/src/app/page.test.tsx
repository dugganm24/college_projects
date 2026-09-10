import { expect, test, vi } from 'vitest'
import { render, screen, fireEvent, cleanup } from '@testing-library/react'
import Home from './page'


test('Rendering', () => {
    const { getByText } = render(<Home />)
    const swapElement = getByText('Swap')
    const undoElement = getByText('Undo')
    const resetElement = getByText('Reset')
    const config1Element = getByText('Game 1')
    const config2Element = getByText('Game 2')
    const config3Element = getByText('Game 3')

    expect(resetElement === undefined).toBe(false)
    expect(resetElement.tagName).toBe('BUTTON')
    expect(swapElement === undefined).toBe(false)
    expect(swapElement.tagName).toBe('BUTTON')
    expect(config1Element === undefined).toBe(false)
    expect(config1Element.tagName).toBe('BUTTON')
    expect(config2Element === undefined).toBe(false)
    expect(config2Element.tagName).toBe('BUTTON')
    expect(config3Element === undefined).toBe(false)
    expect(config3Element.tagName).toBe('BUTTON')
    expect(undoElement === undefined).toBe(false)
    expect(undoElement.tagName).toBe('BUTTON')
    cleanup();
});

test('buttonTesting', async () => {
    const { getByText } = render(<Home />)
    const refresh = vi.fn()

    const config1Element = getByText('Game 1')
    const config2Element = getByText('Game 2')
    const undoElement = getByText('Undo')
    const swapElement = getByText('Swap')
    const resetElement = getByText('Reset')

    fireEvent.click(config1Element)
    expect(screen.getByText(/Swaps: 0/)).toBeDefined()

    fireEvent.click(resetElement)
    expect(screen.getByText('un')).toBeDefined()

    fireEvent.click(config2Element);
    expect(screen.getByText('ex')).toBeDefined()
    const syllable1 = screen.getByText('ex')
    const syllable2 = screen.getByText('for')

    fireEvent.click(syllable1)
    fireEvent.click(syllable2)

    fireEvent.click(swapElement)
    expect(screen.getByText(/Swaps: 1/)).toBeDefined()

    fireEvent.click(undoElement)
    expect(screen.getByText(/Swaps: 0/)).toBeDefined()

});

