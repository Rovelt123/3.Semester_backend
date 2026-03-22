package app.daos;

import app.SetupTest;
import app.entities.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResponseDAOTest extends SetupTest {

    @Test
    void getByShiftRequestId() {
        User user = userDAO.create(testUser);

        ShiftRequest request = new ShiftRequest();
        shiftRequestDAO.create(request);

        Response response = new Response();
        response.setUser(user);
        response.setShiftRequest(request);
        responseDAO.create(response);

        List<Response> results = responseDAO.getByShiftRequestId(request.getId());

        assertEquals(1, results.size());
    }

    // ________________________________________________________

    @Test
    void getByUserAndShiftRequestId() {
        User user = userDAO.create(testUser);

        ShiftRequest request = new ShiftRequest();
        shiftRequestDAO.create(request);

        Response response = new Response();
        response.setUser(user);
        response.setShiftRequest(request);
        responseDAO.create(response);

        Response found = responseDAO.getByUserAndShiftRequestId(user.getId(), request.getId());

        assertNotNull(found);
        assertEquals(user.getId(), found.getUser().getId());
    }
}