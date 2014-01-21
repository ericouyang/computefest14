#include <iostream>
#include <string>
#include <vector>

#include "PlayerGame.hpp"

/** Pretty print a game wall */
void print_wall(const Game::wall_type& s)
{
  printf("  ||");
  for (unsigned i = 0; i < s[0].size(); ++i) printf(" %2d |", i);
  printf("|\n%s\n", std::string(4+5*s[0].size()+1,'=').c_str());
  for (unsigned i = s.size()-1; i > 0; --i) {
    printf("%c ||", 'A' + i);
    for (unsigned j = 0; j < s[i].size(); ++j) printf(" %2d |", s[i][j]);
    printf("|\n");
    printf("--||");
    for (unsigned j = 0; j < s[i].size(); ++j) printf("----|");
    printf("|\n");
  }
  printf("%c ||", 'A');
  for (unsigned j = 0; j < s[0].size(); ++j) printf(" %2d |", s[0][j]);
  printf("|\n%s\n", std::string(4+5*s[0].size()+1,'=').c_str());
}

/** Choose the known brick in the discard or a unknown brick in the pile.
 *   Input:
 *     wall:  A 2D array denoting our game wall.
 *     owall: A 2D array denoting the opponent's game wall.
 *     discard_brick: The integer value of the known brick in the discard.
 *   Output:
 *     'd': Accept the known brick in the discard.
 *     'p': Reject the known brick in the discard and draw from the pile.
 */
std::string choose_discard_or_pile(const Game::wall_type& wall,
                                const Game::wall_type& owall,
                                int discard_brick)
{
  std::cout << "\nOpponent:" << std::endl;
  print_wall(owall);
  std::cout << "\nMy Wall:" << std::endl;
  print_wall(wall);

  std::cout << "\np: **  d: " << std::setw(2) << discard_brick << std::endl;
  std::cout << "Pile or Discard: ";
  std::string pd;
  std::cin >> pd;
  return pd;
}

/** Convert numeric (row, col) to alphanumeric "XY" */
std::string rowcol2coord(int row, int col)
{
  return std::string(1, 'A' + row) + std::string(1, '0' + col);
}

/** Choose the coordinate to place the chosen brick into our wall.
 * Input:
 *   wall:  A 2D array denoting our wall.
 *   owall: A 2D array denoting the opponent's wall.
 *   brick: The integer value of the chosen brick.
 * Output:
 *   'XY' where X is a char from 'A' to 'D' and Y is an char from '0' to '3'
 *   Consider using rowcol2coord to produce valid output.
 */
std::string choose_coord(const Game::wall_type& wall,
                         const Game::wall_type& owall,
                         int brick)
{
  std::cout << brick << std::endl;
  std::cout << "Coord: ";
  std::string move;
  std::cin >> move;
  return move;
}


int main(int argc, char** argv)
{
  if (argc != 2) {
    std::cout << "Usage: " << argv[0] << " GAMEID" << std::endl;
    std::cout << "\tGAMEID = 0     creates a new game" << std::endl;
    std::cout << "\tGAMEID = WXYZ  connect to a specific game" << std::endl;
    exit(0);
  }

  // Connect to a FoosGame with id from the command line
  Game game(argv[1]);

  while (true) {
    // On our turn, we get the brick on the pile
    int pile = game.get_discard();

    // Choose 'p' to get the pile brick or 'd' to get a random brick
    std::string pd_move = choose_discard_or_pile(game.wall, game.owall, pile);

    // Get the brick we chose (either the pile brick or a random brick)
    int brick = game.get_brick(pd_move);

    // Determine where to place this brick with row-col coords "A0", "B3", etc
    std::string co_move = choose_coord(game.wall, game.owall, brick);

    // Make the move -- informs the opponent and updates the wall
    game.make_move(co_move);
  }

  return 0;
}
