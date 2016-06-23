package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sotechat.service.AdminService;

@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/newuser", method = RequestMethod.POST)
    public String addNewUser(String jsonPerson) {
        adminService.addUser(jsonPerson);
        return "redirect:/getusers";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
    public String getAllUsers() {
        return adminService.listAllPersonsAsJsonList();
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return "redirect:/getusers";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/resetpassword/{id}", method = RequestMethod.POST)
    public String resetPassword(@PathVariable String id, String newPassword) {
        adminService.changePassword(id, newPassword);
        return "redirect:/getusers";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/tyhjennaTietokantaDemoamistaVarten", method = RequestMethod.GET)
    public String resetDatabase() {
        adminService.resetDatabase();
        return "Tyhjennetaan tietokantaa... Kokeile menna etusivulle.";
    }


}
