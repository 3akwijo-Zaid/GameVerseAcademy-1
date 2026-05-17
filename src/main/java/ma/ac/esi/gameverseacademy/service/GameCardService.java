package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.GameCard;
import ma.ac.esi.gameverseacademy.repository.GameCardRepository;
import java.util.List;

public class GameCardService {
    private GameCardRepository gameCardRepository = new GameCardRepository();

    public List<GameCard> getAllCards() {
        return gameCardRepository.getAllCards();
    }

    public List<GameCard> getCardsByUser(String userLogin) {
        return gameCardRepository.getCardsByUser(userLogin);
    }

    public int addCard(GameCard gc) {
        return gameCardRepository.addCard(gc);
    }

    public GameCard getCardById(int id) {
        return gameCardRepository.getCardById(id);
    }

    public void deleteCard(int id) {
        gameCardRepository.deleteCard(id);
    }
}
