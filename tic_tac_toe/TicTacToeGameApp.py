from TicTacToeClass import (
    TicTacToe,
    query_player,
    minmax_decision,
    alpha_beta_search,
    alpha_beta_cutoff_search,
    monte_carlo_tree_search,
    random_player,
    playerValues,
)

#function to get user input for player definition
def get_player(prompt):
    while True:
        try:
            player_input = int(input(prompt))
            if player_input in playerValues.values():
                return player_input  
            else:
                print("Could not find your player, please try again: ", end='')
        except ValueError:
            print("Invalid input, please enter a number corresponding to the player: ")

#function to get algorithm based on player type 
def get_action(player, game, state):
    if player == playerValues['Random']:
        return random_player(game, state)
    elif player == playerValues['MiniMax']:
        return minmax_decision(state, game)
    elif player == playerValues['AlphaBeta']:
        return alpha_beta_search(state, game)
    elif player == playerValues['HeuristicAlphaBeta']:
        return alpha_beta_cutoff_search(state, game)
    elif player == playerValues['MCTS']:
        return monte_carlo_tree_search(state, game)
    elif player == playerValues['Query']:
        return query_player(game, state)

#function to play a round of the game
def play_round(game, state, player1, player2):
    while not game.terminal_test(state):
        current_player = state.to_move  

        #get action based on player type 
        if current_player == 'X':
            action = get_action(player1, game, state)
            print(f"The action taken by Player X is: {action}")
        else:  #current player O
            action = get_action(player2, game, state)
            print(f"The action taken by Player O is: {action}")


        #update game state based on chosen action
        state = game.result(state, action)
        game.display(state)  #display updated game state
        
        utility = game.compute_utility(state.board, action, current_player)
        
        if current_player == 'X':
            print(f"Player X's utility: {utility}")
        else:  #current player O
            print(f"Player O's utility: {abs(utility)}")
        print("\n")

    return state.utility  #return utility of game state

#function to check if user wants to play again
def playAgain(prompt):
    while True:
        try:
            play_again = input(prompt).strip().lower()
            if play_again not in ['yes', 'no']:
                print("Invalid input. Please enter 'yes' or 'no'.")
                continue
            else:
                return play_again
        except ValueError:
            print("Invalid input. Please enter 'yes' or 'no'.")

#main
def main():
    while True:
        game = TicTacToe()  #set game 
        rounds_won = {'X': 0, 'O': 0}  #used to track number of round won
        state = game.initial  #set initial state 

        print("Player Selection: \n")
        print("1. Random Player")
        print("2. MiniMax Player")
        print("3. Alpha Beta Player")
        print("4. Heuristic Alpha Beta Player")
        print("5. MCTS Player")
        print("6. Query Player")

        #get player input 
        player1 = get_player("Please enter your first player: ")
        player2 = get_player("Please enter your second player: ")

       #play three rounds of the game (if necessary)
        for round_number in range(1, 4):
            print(f"\nRound {round_number}")
            result = play_round(game, state, player1, player2)

            #player X wins (utility = 1)
            if result == 1:
                print("Player X won the game in round ", round_number)
                print("\n")
                rounds_won['X'] += 1
            #player O wins (utility = -1)
            elif result == -1:
                print("Player O won the game in round ", round_number)
                print("\n")
                rounds_won['O'] += 1
            #draw (utility = 0 for both players)
            elif result == 0:
                print("Player X and Player O drew the game in round ", round_number)
                print("\n")

            #check if either player has won 2 rounds
            if rounds_won['X'] == 2:
                print("Player X can win two out of three rounds in the game.")
                print("Player X is the winner.")
                break  #terminate
            elif rounds_won['O'] == 2:
                print("Player O can win two out of three rounds in the game.")
                print("Player O is the winner.")
                break  #terminate

        #check if no player can win two out of three rounds
        if rounds_won['X'] < 2 and rounds_won['O'] < 2:
            print("No Player can win two out of three rounds in the game.")
            print("The game was a draw.")

        #check if user wants to play again
        play_again = playAgain("\nWould you like to play again? ")
        
        if play_again == 'no':
            print("Thank You for Playing Our Game!")
            break
        elif play_again == 'yes':
            continue

if __name__ == '__main__':
    main()
