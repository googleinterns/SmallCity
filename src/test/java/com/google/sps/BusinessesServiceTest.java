import java.util.List;
import java.util.LinkedList;
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
import com.google.maps.model.Geometry;
import static org.Mockito.*;

@RunWith(JUnit4.class)
public final class BusinessesServiceTest {
/*
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
  */
  private final int MIN_LISTINGS_RETURNED = 20;
  private final int MAX_LISTINGS_RETURNED = 60;

  private final MapLocation TEST_MAP_LOCATION = new MapLocation(39.459836, -76.742536);
  private final String TEST_PRODUCT = "pizza";

  private final List<Listing> TEST_PASSED_LIST = new LinkedList<>();

  private List<Listing> TEST_TEXT_SEARCH_RESULT = new LinkedList<>(Arrays.asList(new Listing("Pizza Connection", 
        "11215 York Rd, Cockeysville, MD 21030, United States", 
        null,
        4.4,
        null,
        null), 
        new Listing("Brookside's Pizzeria",
        "9419 Common Brook Rd #109, Owings Mills, MD 21117, United States",
        null,
        4.2,
        null,
        null),
        new Listing("Village Pizza",
        "38 Main St, Reisterstown, MD 21136, United States",
        null,
        4.2,
        null,
        null)));


@Test
public void testGetBusinessesFromTextSearch() {
  BusinessesService mockedBusinessesService = mock(BusinessesService.class);
  BusinessesService testBusinessesService = new BusinessesService(TEST_PASSED_LIST);
  when(mockedBusinessesService.getBusinessesFromTextSearch(TEST_MAP_LOCATION, TEST_PRODUCT))
        .thenReturn(TEST_TEXT_SEARCH_RESULT); 

  List<Listing> expected = mockedBusinessesService
        .getBusinessesFromTextSearch(TEST_MAP_LOCATION, TEST_PRODUCT);
  List<Listing> actual = testBusinessesService
        .getBusinessesFromTextSearch(TEST_MAP_LOCATION, TEST_PRODUCT);

  Assert.assertTrue(actual.size() >= MIN_LISTINGS_RETURNED);
  Assert.assertTrue(actual.size() <= MAX_LISTINGS_RETURNED);
  
  boolean containsExpected = false;
  for (Listing expectedListing : expected) {
    containsExpected = false;
    for (Listing actualListing : actual) {
      if (expectedListing.name == actualListing.name) {
        containsExpected = true;
        break;
      }
    }
    if (containsExpected == false) {
      break;
    }
  }

  Assert.assertTrue(containsExpected);
}
}