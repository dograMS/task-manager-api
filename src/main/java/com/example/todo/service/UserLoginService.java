package com.example.todo.service;


import com.example.todo.dto.CredentialUpdateDTO;
import com.example.todo.model.UserLogin;
import com.example.todo.repo.UserLoginRepo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.CredentialNotFoundException;
import java.util.UUID;

@Data
@Service
public class UserLoginService implements UserDetailsService {

    @Autowired
    private UserLoginRepo ulRepo;

    @Autowired
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder ;

    public UserLogin addUser(String username, String email, String pass) throws Exception {
        UserLogin newUser = new UserLogin();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(pass));
        newUser.setUsername(username);

        UserLogin existingUser = ulRepo.findCredentialMatch(newUser);
        System.out.println(existingUser);
        if(existingUser == null){
            return ulRepo.save(newUser);
        }

        if(existingUser.getUsername().equals(newUser.getUsername())){
            throw new Exception("user already exists");
        }
        else if(existingUser.getEmail().equals(newUser.getEmail())){
            throw new Exception("email already used");
        }else{
            throw new IllegalStateException("something went wrong");
        }

    }

    public UserLogin loginUser(String username, String pass) throws CredentialNotFoundException {
        UserLogin user = ulRepo.findByUname(username);

        if(user == null){
            throw new CredentialNotFoundException("user not found");
        }
        else if(!passwordEncoder.matches(pass, user.getPassword())){
            throw new CredentialNotFoundException("wrong password");
        }

        return user;

    }

    public void deleteUser(String username, String pass) throws Exception {
        UserLogin user = ulRepo.findByUname(username);

        if(user == null){
            throw new AccountNotFoundException("acound not found");
        }else if(!user.getPassword().equals(pass)){
            throw new Exception("wrong password");
        }

        ulRepo.deleteUserById(user.getUuid());
    }

    public boolean matchCredentials(UserLogin authUser, UserLogin user) throws CredentialNotFoundException {
        if((authUser.getUsername() == user.getUsername() || authUser.getEmail() == user.getEmail()) && authUser.getPassword() == authUser.getPassword()){
            return true;
        }else{
            throw new CredentialNotFoundException("Credentials didn't match");
        }

    }

    public void updateUserName(UUID uuid, String username) throws Exception {


        var oldUser = ulRepo.findByUuid(uuid);
        if(!oldUser.isPresent()){
            throw new CredentialNotFoundException("user not found");
        }

        UserLogin updatedUser = oldUser.get();
        updatedUser.setUsername(username);

        UserLogin existingUser = ulRepo.findCredentialMatch(updatedUser);

        if(existingUser == null){
            ulRepo.save(updatedUser);
        }else{
            throw new Exception("user already exist with this username");
        }

    }

    public void updateEmail(UUID uuid, String email) throws Exception {
        var oldUser = ulRepo.findByUuid(uuid);
        if(oldUser == null){
            throw new Exception("user not found");
        }

        UserLogin updatedUser = oldUser.get();
        updatedUser.setEmail(email);

        UserLogin existingUser = ulRepo.findCredentialMatch(updatedUser);

        if(existingUser == null){
            ulRepo.save(updatedUser);
        }else{
            throw new Exception("email alredy used");
        }
    }

    public void updatePassword(UUID uuid, String pass) throws Exception {
        var oldUser = ulRepo.findByUuid(uuid);
        if(!oldUser.isPresent()){
            throw new Exception("user not found");
        }

        UserLogin updatedUser = oldUser.get();
        updatedUser.setPassword(pass);

        ulRepo.save(updatedUser);
    }

    @Override
    public UserLogin loadUserByUsername(String username) throws UsernameNotFoundException {

        UserLogin user = ulRepo.findByUname(username);
        if(user == null)
            throw new UsernameNotFoundException("User with id "+ username+ " not found");

        return user;
    }

    public UserLogin loadUserByEmail(String email) throws UsernameNotFoundException{

        UserLogin user = ulRepo.findByUname(email);
        if(user == null)
            throw new UsernameNotFoundException("User with id "+ email+ " not found");

        return user;
    }

    public String updateUserDetails(UserLogin authUser, UserLogin newDetailsUser) throws Exception {

        boolean changed = false;

        if(authUser == null){
            throw new CredentialNotFoundException("User not Authorized");
        }
        if(!newDetailsUser.getUsername().isEmpty()){
            updateUserName(authUser.getUuid(), newDetailsUser.getUsername());
            authUser.setUsername(newDetailsUser.getUsername());
            changed = true;
        }
        if(!newDetailsUser.getEmail().isEmpty()){
            updateEmail(authUser.getUuid(), newDetailsUser.getUsername());
            authUser.setEmail(newDetailsUser.getEmail());
            changed = true;
        }

        if(changed){
            return jwtService.genTokens(authUser.getUsername());
        }else{
            throw new Exception("No changes done!!");
        }

    }

    public void updateUserCreadentials(UserLogin authUser, CredentialUpdateDTO newCredentials) throws Exception {

        if(authUser == null){
            throw new CredentialNotFoundException("Your aren't Authrorized to take action");
        }

        if(newCredentials.equals(newCredentials.getNewPassword())){
            throw new CredentialNotFoundException("New Password cant be same as old");
        }

        if(authUser.getPassword().equals(newCredentials.getOldPassoword())){
            updatePassword(authUser.getUuid(), newCredentials.getNewPassword());
            authUser.setPassword(newCredentials.getNewPassword());
        }

    }
}
