
package sotechat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import sotechat.service.AdminService;


/**
 * Reitittaa adminin tekemat pyynnot.
 */
@RestController
public class AdminController {

    /** Logiikka pyyntojen kasittelyyn. */
    @Autowired
    private AdminService adminService;

    /** Pyynto ammattilaiskayttajien listaamiseen.
     * @return Listaus JSON-taulukkona. Esimerkki: [{"userId":"admin","username":"pauli","loginName":"admin","conversationsOfPerson":[]},{"userId":"666","username":"Hoitaja","loginName":"hoitaja","conversationsOfPerson":[]}]
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
    public String getAllUsers() {
        return adminService.listAllPersonsAsJsonList();
    }

    /** Pyynto uuden ammattilaiskayttajan luomiseen.
     * @param encodedJsonPerson encodattu json luotavan kayttajan tiedoista
     * @return json {"status":"OK"} tai {"error":"reason"}
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/newuser", method = RequestMethod.POST)
    @ResponseBody
    public String addNewUser(@RequestBody final String encodedJsonPerson) {
        return jsonifiedResponse(adminService.addUser(encodedJsonPerson));
    }

    /** Pyynto ammattilaiskayttajan tilin poistamiseen.
     * @param id poistettavan kayttajan userId
     * @return json {"status":"OK"} tai {"error":"reason"}
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable final String id) {
        return jsonifiedResponse(adminService.deleteUser(id));
    }

    /** Adminin tekema pyynto ammattilaiskayttajan salasanan vaihtamiseksi.
     * @param id userId vaihdon kohteelle (voi olla eri kuin pyynnon tekija)
     * @param encodedPass uusi salasana encodattuna
     * @return json {"status":"OK"} tai {"error":"reason"}
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/resetpassword/{id}", method = RequestMethod.POST)
    public String resetPassword(
            @PathVariable final String id,
            @RequestBody final String encodedPass
    ) {
        return jsonifiedResponse(adminService.changePassword(id, encodedPass));
    }

    /** Tarkoitettu tehtavaksi vain ennen softan demoamista.
     * @return json {"status":"OK"} tai {"error":"reason"}
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/tuhoaHistoria", method = RequestMethod.DELETE)
    public String resetDatabase() {
        return jsonifiedResponse(adminService.clearHistory());
    }

    /** Muotoilee lahetettavan vastauksen.
     * @param error virheilmoitus Stringina tai tyhja String jos kaikki ok.
     * @return JSON muotoiltu vastaus {"status":"OK"} tai {"error":"bla"}
     */
    private String jsonifiedResponse(final String error) {
        if (error.isEmpty()) {
            return "{\"status\":\"OK\"}";
        }
        return "{\"error\": \"" + error + "\"}";
    }

}
