package ma.ac.esi.gameverseacademy.service;

import ma.ac.esi.gameverseacademy.model.User;
import ma.ac.esi.gameverseacademy.repository.UserRepository;

public class UserService {
    private UserRepository userRepository = new UserRepository();

    public User findUserByCredentials(String login, String password) {

        return userRepository.getUserByCredentials(login, password);
    }
}