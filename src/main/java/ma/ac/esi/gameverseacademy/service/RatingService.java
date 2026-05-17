package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.Rating;
import ma.ac.esi.gameverseacademy.repository.RatingRepository;
import java.util.List;

public class RatingService {
    private RatingRepository ratingRepository = new RatingRepository();

    public void addOrUpdateRating(Rating rating) {
        ratingRepository.addOrUpdateRating(rating);
    }

    public List<Rating> getRatingsByMod(int modId) {
        return ratingRepository.getRatingsByMod(modId);
    }
}
