package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import sotechat.service.AdminService;
import org.springframework.util.Base64Utils;

@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/newuser", method = RequestMethod.POST)
    public @ResponseBody String addNewUser(@RequestBody String jsonPerson) {
        try {
            adminService.addUser(new String(Base64Utils
                    .decodeFromString(jsonPerson)));
        } catch (Exception e) {
            return "{\"error\":\"Käyttäjää ei voitu lisätä. " +
                    "Tarkista, että kirjautumisnimi tai palvelunimi eivät " +
                    "ole jo varattuja.\"}";
        }
        return statusOK();
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
    public String getAllUsers() {
        return adminService.listAllPersonsAsJsonList();
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable String id) throws Exception {
        if (adminService.deleteUser(id)) {
            return statusOK();
        } else {
            return noSuchUser();
        }
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/resetpassword/{id}", method = RequestMethod.POST)
    public String resetPassword(@PathVariable String id,
                                @RequestBody String newPassword) throws Exception {
        if (adminService.changePassword(id, new String(Base64Utils.decodeFromString(newPassword)))) {
            return statusOK();
        } else {
            return noSuchUser();
        }
    }

    /**
     * Tarkoitettu tehtavaksi vain ennen softan demoamista.
     * @return tieto onnistumisesta JSONina.
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/tuhoaHistoria", method = RequestMethod.GET)
    public String resetDatabase() {
        return jsonifiedResponse(adminService.clearHistory());
    }

    private String jsonifiedResponse(final String error) {
        if (error.isEmpty()) {
            return statusOK();
        }
        return "{\"error\": \"" + error + "\"}";
    }

    private String statusOK() {
        return "{\"status\":\"OK\"}";
    }

    private String noSuchUser() {
        return "{\"error\": \"No such user\"}";
    }
}
