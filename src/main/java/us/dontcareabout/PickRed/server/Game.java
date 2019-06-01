package us.dontcareabout.PickRed.server;

import us.dontcareabout.PickRed.shared.Card;
import us.dontcareabout.PickRed.shared.GMT;
import us.dontcareabout.PickRed.shared.Player;
import us.dontcareabout.PickRed.shared.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Game {
	private HashMap<Player, PickRedPlayer> players = new HashMap<>();
	private Card[] cards;
	private int cardIdx = 0;
	private int playerNumber;
	private ArrayList<Card> cardsOnDesk = new ArrayList<>();

	public Game(ArrayList<Player> players) {
		cards = Card.genDeck();
		GMT.shuffling(cards);
		initGame(players);
	}

	Game(ArrayList<Player> players, Card[] cards) {
		this.cards = cards;
		initGame(players);
	}

	private void initGame(ArrayList<Player> players) {
		playerNumber = players.size();

		for (Player player : players) {
			this.players.put(player, new PickRedPlayer(player));
		}

		dealCard(players);

		cardsOnDesk.add(drawCard());
		cardsOnDesk.add(drawCard());
		cardsOnDesk.add(drawCard());
		cardsOnDesk.add(drawCard());
	}

	public void addCardsOnDesk(Card card) {
		cardsOnDesk.add(card);
	}

	public void removeCardsOnDesk(Card card) {
		cardsOnDesk.remove(card);
	}

	public Card drawCard() {
		cardIdx += 1;
		return cards[cardIdx - 1];
	}

	public void playHandCard(Player player, Card card) {
		players.get(player).playCard(card);
	}

	private void dealCard(ArrayList<Player> players) {
		int handCardNumber = 24 / playerNumber;

		for (int j = 0; j < handCardNumber; j++) {
			for (Player eachPlayer : players) {
				this.players.get(eachPlayer).recieveCard(drawCard());
			}
		}
	}

	/**
	 * 玩家撿牌。
	 *
	 * @return true 當兩張牌皆小於 10 且相加等於 10 或兩張牌皆大於 10 且相等
	 */
	public boolean pick(Player player, Card card1, Card card2) {
		if (card1.number < 10 && card1.number + card2.number != 10) {
			return false;
		}
		if (card1.number >= 10 && card1.number != card2.number) {
			return false;
		}
		players.get(player).pickCard(card1, card2);
		return true;
	}

	/**
	 * @return 一張牌的分數
	 */
	public int score(Card card) {
		if (card.number == 1) {
			switch (card.suit) {
				case club:
					return 40;
				case spade:
					return 30;
				case heart:
					return 20;
				case diamond:
					return 20;
			}
		} else if (card.suit == Suit.heart || card.suit == Suit.diamond) {
			if (card.number < 10) {
				return card.number;
			} else {
				return 10;
			}
		}

		return 0;
	}


	/**
	 * 依照牌的分數由大到小排序。
	 */
	public void sortCardsByScore(ArrayList<Card> cards) {
		int size = cards.size();

		for (int i = size - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (score(cards.get(j)) < score(cards.get(j + 1))) {
					Card temp = cards.get(j);
					cards.set(j, cards.get(j + 1));
					cards.set(j + 1, temp);
				}
			}
		}
	}


	public List<Card> getHandCards(Player player) {
		List<Card> cards = players.get(player).getHandCards();
		return Collections.unmodifiableList(cards);
	}

	public List<Card> getPickedCards(Player player) {
		List<Card> cards = players.get(player).getPickedCards();
		return Collections.unmodifiableList(cards);
	}

	public List<Card> getCardsOnDesk() {
		return Collections.unmodifiableList(cardsOnDesk);
	}
}
