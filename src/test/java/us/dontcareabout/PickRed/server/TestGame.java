package us.dontcareabout.PickRed.server;


import us.dontcareabout.PickRed.exception.TestFailedException;
import us.dontcareabout.PickRed.shared.Card;
import us.dontcareabout.PickRed.shared.Player;
import us.dontcareabout.PickRed.shared.Suit;

import java.util.ArrayList;

public class TestGame {

	public static void main(String[] args) {
		testFooPlayer();
		testGame();

		int[] testPlayerNumber = {2, 3, 4};
		for (int playerNumber : testPlayerNumber) {
			autoTestGamePlay(playerNumber);
		}

		testGamePlay();
	}

	private static void testGame() {
		System.out.println("測試 Game");
		ArrayList<Player> players = new ArrayList<>();
		Player player1 = new Player("1", "1");
		Player player2 = new Player("2", "2");
		players.add(player1);
		players.add(player2);

		Game game = new Game(players, Card.genDeck());


		// 測試 constructor / initGame()
		System.out.println("測 桌牌 / initGame() ");
		test(game.getCardsOnDesk(), "[heart[12], heart[13], diamond[1], diamond[2]]");

		// 測試 drawCard()
		System.out.println("測 drawCard() ");
		Card card1 = game.drawCard();
		Card card2 = game.drawCard();
		test(card1, new Card(Suit.diamond, 3));
		test(card2, new Card(Suit.diamond, 4));

		// 測試 addCardsOnDesk
		System.out.println("測 addCardsOnDesk() ");
		game.addCardsOnDesk(card1);
		test(game.getCardsOnDesk(), "[heart[12], heart[13], diamond[1], diamond[2], diamond[3]]");
		game.addCardsOnDesk(card2);
		test(game.getCardsOnDesk(), "[heart[12], heart[13], diamond[1], diamond[2], diamond[3], diamond[4]]");

		// 測試 removeCardsOnDesk
		System.out.println("測 removeCardsOnDesk() ");
		game.removeCardsOnDesk(card1);
		test(game.getCardsOnDesk(), "[heart[12], heart[13], diamond[1], diamond[2], diamond[4]]");
		game.removeCardsOnDesk(card2);
		test(game.getCardsOnDesk(), "[heart[12], heart[13], diamond[1], diamond[2]]");

		// 測試 pick()
		System.out.println("測 pick()");
		boolean res;
		res = game.pick(player1, new Card(Suit.diamond, 1), new Card(Suit.diamond, 1));
		test(res, false);
		res = game.pick(player1, new Card(Suit.diamond, 1), new Card(Suit.diamond, 9));
		test(res, true);
		res = game.pick(player2, new Card(Suit.club, 11), new Card(Suit.spade, 11));
		test(res, true);
		test(game.getPickedCards(player1), "[diamond[1], diamond[9]]");
		test(game.getPickedCards(player2), "[club[11], spade[11]]");
	}

	private static void autoTestGamePlay(int playerNumber) {
		ArrayList<Player> players = new ArrayList<>();
		for (int i = 0; i < playerNumber; i++) {
			players.add(new Player(String.valueOf(i), String.valueOf(i)));
		}

		TestCase testCase = new TestCase(playerNumber);

		Game game = new Game(players, Card.genDeck());

		// 測試 桌牌
		System.out.println("測試 桌牌");
		test(game.getCardsOnDesk(), testCase.expCardsOnDesk);

		// 測試 玩家手牌
		System.out.println("測試 玩家手牌");
		ArrayList<String> handCards = new ArrayList<>();
		for (Player eachPlayer : players) {
			handCards.add(game.getHandCards(eachPlayer).toString());
		}
		test(handCards, testCase.handCards);

		int turn = 0;

		while (turn < 24) {
			int playerIdx = turn % playerNumber;
			Player player = players.get(playerIdx);

			Card card1 = game.getHandCards(player).get(0);
			game.playHandCard(player, card1);
			Card card2 = game.drawCard();

			for (Card card : new Card[]{card1, card2}) {
				ArrayList<Card> sortedCards = new ArrayList<Card>(game.getCardsOnDesk());
				game.sortCardsByScore(sortedCards);

				boolean picked = false;
				for (Card cardDesk : sortedCards) {
					if (game.pick(player, card, cardDesk)) {
						game.removeCardsOnDesk(cardDesk);
						picked = true;
						break;
					}
				}

				if (!picked) {
					game.addCardsOnDesk(card);
				}
			}

			turn += 1;
		}

		// 測試 撿牌
		System.out.println("測試 撿牌");
		ArrayList<String> pickedCards = new ArrayList<>();
		for (Player eachPlayer : players) {
			pickedCards.add(game.getPickedCards(eachPlayer).toString());
		}
		test(pickedCards, testCase.cardsPicked);
	}

	private static void testGamePlay() {
		ArrayList<Player> players = new ArrayList<>();

		players.add(new Player("1", "1"));
		players.add(new Player("2", "2"));
		players.add(new Player("3", "3"));

		int playerNumber = players.size();

		Game game = new Game(players, Card.genDeck());

		int turn = 0;

		while (turn < 24) {
			int playerIdx = turn % playerNumber;
			Player player = players.get(playerIdx);

			System.out.println("\nCards on desk:\n" + game.getCardsOnDesk());

			for (Player eachPlayer : players) {
				System.out.println(eachPlayer.name + ", " + game.getHandCards(eachPlayer));
			}

			Card card1 = game.getHandCards(player).get(0);
			game.playHandCard(player, card1);
			System.out.println(player.name + " plays card: " + card1);

			Card card2 = game.drawCard();
			System.out.println("Drew card: " + card2);

			for (Card card : new Card[]{card1, card2}) {
				ArrayList<Card> sortedCards = new ArrayList<Card>(game.getCardsOnDesk());
				game.sortCardsByScore(sortedCards);

				boolean picked = false;
				for (Card cardDesk : sortedCards) {
					if (game.pick(player, card, cardDesk)) {
						game.removeCardsOnDesk(cardDesk);
						picked = true;
						break;
					}
				}

				if (!picked) {
					game.addCardsOnDesk(card);
				}
			}

			turn += 1;
		}

		System.out.println("\nPicked cards:");
		for (Player eachPlayer : players) {
			System.out.println(eachPlayer.name + ", " + game.getPickedCards(eachPlayer));
		}
	}

	private static void testFooPlayer() {
		Player player1 = new Player("1", "A");
		PickRedPlayer pickRedPlayer1 = new PickRedPlayer(player1);

		Card card1 = new Card(Suit.diamond, 1);
		Card card2 = new Card(Suit.heart, 13);
		Card card3 = new Card(Suit.spade, 5);
		pickRedPlayer1.recieveCard(card1);
		pickRedPlayer1.recieveCard(card2);
		pickRedPlayer1.recieveCard(card3);

		// 測試 手牌
		test(pickRedPlayer1.getHandCards(), "[diamond[1], heart[13], spade[5]]");

		// 測試 出牌
		pickRedPlayer1.playCard(card1);
		pickRedPlayer1.playCard(card2);
		test(pickRedPlayer1.getHandCards(), "[spade[5]]");

		// 測試 撿牌
		pickRedPlayer1.pickCard(card1, card2);
		test(pickRedPlayer1.getPickedCards(), "[diamond[1], heart[13]]");
	}

	private static void test(Object test, Object exp) {
		if ((test.toString().equals(exp.toString()))) {
			System.out.println("Pass!");
		} else {
			String msg = "failed! " + "Expect: " + exp.toString() +
					",\n but get: " + test.toString();
			throw new TestFailedException(msg);
		}
	}
}

class TestCase {
	public String expCardsOnDesk;
	public String handCards;
	public String cardsPicked;

	public TestCase(int playerNumber) {
		switch (playerNumber) {
			case 2:
				player2();
				break;
			case 3:
				player3();
				break;
			case 4:
				player4();
		}
	}

	private void player4() {
		System.out.println("test 4 players");
		expCardsOnDesk = "[heart[12], heart[13], diamond[1], diamond[2]]";

		handCards = "[[spade[1], spade[5], spade[9], spade[13], heart[4], heart[8]], " +
				"[spade[2], spade[6], spade[10], heart[1], heart[5], heart[9]], " +
				"[spade[3], spade[7], spade[11], heart[2], heart[6], heart[10]], " +
				"[spade[4], spade[8], spade[12], heart[3], heart[7], heart[11]]]";

		cardsPicked = "[[spade[5], diamond[5], diamond[7], diamond[3], spade[9], diamond[1], club[6], heart[4], heart[8], club[2]], " +
				"[spade[6], spade[4], diamond[8], diamond[2], spade[10], diamond[10], diamond[12], heart[12], heart[5], club[5], club[7], heart[3], heart[9], heart[1]], " +
				"[spade[7], spade[3], diamond[9], spade[1], spade[11], diamond[11], diamond[13], heart[13], heart[6], club[4], club[8], heart[2], heart[10], club[10], club[12], spade[12]], " +
				"[diamond[6], diamond[4], spade[8], spade[2], heart[7], club[3], club[9], club[1], heart[11], club[11], club[13], spade[13]]]";
	}

	private void player3() {
		System.out.println("test 3 players");
		expCardsOnDesk = "[heart[12], heart[13], diamond[1], diamond[2]]";

		handCards = "[[spade[1], spade[4], spade[7], spade[10], spade[13], heart[3], heart[6], heart[9]], " +
				"[spade[2], spade[5], spade[8], spade[11], heart[1], heart[4], heart[7], heart[10]], " +
				"[spade[3], spade[6], spade[9], spade[12], heart[2], heart[5], heart[8], heart[11]]]";

		cardsPicked = "[[diamond[6], diamond[4], spade[7], spade[3], diamond[9], spade[1], spade[10], diamond[10], diamond[12], heart[12], heart[6], club[4], club[8], heart[2], heart[9], heart[1]], " +
				"[spade[5], diamond[5], diamond[7], diamond[3], spade[8], spade[2], spade[11], diamond[11], diamond[13], heart[13], club[6], heart[4], heart[7], club[3], club[9], club[1], heart[10], club[10], club[12], spade[12]], " +
				"[spade[6], spade[4], diamond[8], diamond[2], spade[9], diamond[1], heart[5], club[5], club[7], heart[3], heart[8], club[2], heart[11], club[11], club[13], spade[13]]]";
	}

	private void player2() {
		System.out.println("test 2 players");
		expCardsOnDesk = "[heart[12], heart[13], diamond[1], diamond[2]]";

		handCards = "[[spade[1], spade[3], spade[5], spade[7], spade[9], spade[11], spade[13], heart[2], heart[4], heart[6], heart[8], heart[10]], " +
				"[spade[2], spade[4], spade[6], spade[8], spade[10], spade[12], heart[1], heart[3], heart[5], heart[7], heart[9], heart[11]]]";

		cardsPicked = "[[spade[5], diamond[5], diamond[7], diamond[3], spade[7], spade[3], diamond[9], spade[1], spade[9], diamond[1], spade[11], diamond[11], diamond[13], heart[13], club[6], heart[4], heart[6], club[4], club[8], heart[2], heart[8], club[2], heart[10], club[10], club[12], spade[12]], " +
				"[diamond[6], diamond[4], spade[6], spade[4], diamond[8], diamond[2], spade[8], spade[2], spade[10], diamond[10], diamond[12], heart[12], heart[5], club[5], club[7], heart[3], heart[7], club[3], club[9], club[1], heart[9], heart[1], heart[11], club[11], club[13], spade[13]]]";
	}
}