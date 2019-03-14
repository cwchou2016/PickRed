package us.dontcareabout.PickRed.server;

import us.dontcareabout.PickRed.shared.Card;
import us.dontcareabout.PickRed.shared.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Game {
	private ArrayList<Player> players;
	private Card[] cards = Card.genDeck();
	private int cardIdx = 0;
	private ArrayList<Card> cardsOnDesk = new ArrayList<>();

	private Map<Player, ArrayList<Card>> handCard = new HashMap<>();
	private Map<Player, ArrayList<Card>> cardsPicked = new HashMap<>();

	public Game(ArrayList<Player> players) {
		this.players = players;
		for(Card card:cards) {
			System.out.print(card+", ");
		}
		System.out.println();

		dealCard();

		for(int idx =0;idx<4;idx++) {
			System.out.println(handCard.get(players.get(idx)) +", "+cardsPicked.get(players.get(idx)));
		}

		System.out.println(cardsOnDesk);

		pick(players.get(1), drawCard(),drawCard());
		pick(players.get(3), drawCard(),drawCard());
		pick(players.get(0), drawCard(),drawCard());

		for(int idx =0;idx<4;idx++) {
			System.out.println(handCard.get(players.get(idx)) +", "+cardsPicked.get(players.get(idx)));
		}
	}

	private Card drawCard() {
		cardIdx +=1;
		return cards[cardIdx -1];
	}

	private void dealCard() {
		Card[][] tempCards = new Card[4][6];

		for(int j=0;j<6;j++) {
			for(int i=0;i<4;i++) {
				tempCards[i][j] = drawCard();
			}
		}

		for(int idx =0;idx<4;idx++) {
			cardsOnDesk.add(drawCard());
			handCard.put(players.get(idx), new ArrayList<>(Arrays.asList(tempCards[idx])));
			cardsPicked.put(players.get(idx), new ArrayList<Card>());
		}
	}

	/**
	 * 玩家撿牌。
	 */
	private void pick(Player player, Card card1, Card card2) {
		cardsPicked.get(player).add(card1);
		cardsPicked.get(player).add(card2);
	}

	private void score() {}
}
