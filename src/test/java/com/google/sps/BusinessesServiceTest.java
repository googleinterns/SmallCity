import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.SmallCityService;
import com.google.sps.data.UserService;
import com.google.sps.data.Listing;
import com.google.sps.data.MapLocation;
import com.google.sps.data.SearchObject;
import com.google.sps.data.User;
import com.googe.maps.model.PlaceSearchResult;
import com.google.maps.model.Geometry;

@RunWith(JUnit4.class)
public final class BusinessesServiceTest {

  private List<Listing> testBusinesses = new LinkedList<Listing>();
  private BusinessesService testBusinessesService = new BusinessesService(testBusinesses);
  private PlaceSearchResult TEST_PLACE_SEARCH_RESULT = new PlaceSearchResult();
  TEST_PLACE_SEARCH_RESULT.name = "TEST_NAME";
  TEST_PLACE_SEARCH_RESULT.vicinity = "TEST_VICINITY";
  TEST_PLACE_SEARCH_RESULT.formattedAddress = "TEST_FORMATTED_ADDRESS";
  TEST_PLACE_SEARCH_RESULT.geometry = new Geometry();
  TEST_PLACE_SEARCH_RESULT.rating = 0;
  TEST_PLACE_SEARCH_RESULT.photos = null;
  TEST_PLACE_SEARCH_RESULT.types = null;
}

@Test
public void testAddListingToBusinesses() {
  Assert.assertEquals(testBusinesses.size(), testBusinessesService.allBusinesses.size());
  
}