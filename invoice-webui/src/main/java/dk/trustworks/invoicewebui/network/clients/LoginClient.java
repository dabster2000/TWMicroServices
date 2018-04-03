package dk.trustworks.invoicewebui.network.clients;

import dk.trustworks.invoicewebui.model.User;
import dk.trustworks.invoicewebui.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginClient {

    protected static Logger logger = LoggerFactory.getLogger(LoginClient.class.getName());

    @Autowired
    private UserRepository userRepository;

    public User login(String username, String password) {
        logger.info(String.format("User.login(%s)", username));
        User user = userRepository.findByUsername(username);
        if(user.getPassword().trim().equals("")) return null;
        if (BCrypt.checkpw(password, user.getPassword()))
            return user;
        else
            return null;
    }

}
