import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.google.sps.data.BusinessesService;
import com.google.sps.data.Listing;
import com.google.sps.data.MapLocation;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public final class BusinessesServiceTest {

  private final int MIN_LISTINGS_RETURNED = 20;
  private final int MAX_LISTINGS_RETURNED = 60;
  
  // Address location in Owings Mills, MD
  private final MapLocation TEST_MAP_LOCATION = new MapLocation(39.459836, -76.742536);
  private final String TEST_PRODUCT = "pizza";

  private final List<Listing> TEST_PASSED_LIST = new LinkedList<>();

  private BusinessesService mockedBusinessesService = mock(BusinessesService.class);
  private BusinessesService testBusinessesService = new BusinessesService(TEST_PASSED_LIST);
  
  // 3 known returned businesses
  private List<Listing> TEST_TEXT_SEARCH_RESULT = 
        new LinkedList<>(Arrays.asList(new Listing("Pizza Connection", 
        "11215 York Rd, Cockeysville, MD 21030, United States", null,
        4.4, null, null), 
        new Listing("Brookside's Pizzeria",
        "9419 Common Brook Rd #109, Owings Mills, MD 21117, United States", null,
        4.2, null, null),
        new Listing("Village Pizza",
        "38 Main St, Reisterstown, MD 21136, United States", null,
        4.2, null, null)));
  
  // 3 known returned businesses
  private List<Listing> TEST_NEARBY_SEARCH_RESULT = 
        new LinkedList<>(Arrays.asList(new Listing("Perfect Windows Inc",
        "2927 Walnut Avenue, Owings Mills", null,
        0.0, null, null),
        new Listing("Fortuna Liquor & Food",
        "12147 Park Heights Avenue, Owings Mills", null,
        0.0, null, null),
        new Listing("Dantech, Inc",
        "12149 Park Heights Avenue, Owings Mills", null,
        5.0, null, null)));

  @Test
  public void testGetBusinessesFromTextSearch() {
    when(mockedBusinessesService.getBusinessesFromTextSearch(TEST_MAP_LOCATION, TEST_PRODUCT))
          .thenReturn(TEST_TEXT_SEARCH_RESULT); 

    List<Listing> expected = mockedBusinessesService
          .getBusinessesFromTextSearch(TEST_MAP_LOCATION, TEST_PRODUCT);
    List<Listing> actual = testBusinessesService
          .getBusinessesFromTextSearch(TEST_MAP_LOCATION, TEST_PRODUCT);

    Assert.assertTrue(actual.size() >= MIN_LISTINGS_RETURNED);
    Assert.assertTrue(actual.size() <= MAX_LISTINGS_RETURNED);
    
    int containsCounter = 0;
    for (Listing expectedListing : expected) {
      for (Listing actualListing : actual) {
        if (expectedListing.getName().equals(actualListing.getName())) {
          containsCounter++;
        }
      }
    }
    Assert.assertEquals(containsCounter, expected.size());
  }

  @Test
  public void testGetBusinessesFromNearbySearch() {
    when(mockedBusinessesService.getBusinessesFromNearbySearch(TEST_MAP_LOCATION))
          .thenReturn(TEST_NEARBY_SEARCH_RESULT); 
    List<Listing> expected = mockedBusinessesService
          .getBusinessesFromNearbySearch(TEST_MAP_LOCATION);
    List<Listing> actual = testBusinessesService
          .getBusinessesFromNearbySearch(TEST_MAP_LOCATION);

    Assert.assertTrue(actual.size() >= MIN_LISTINGS_RETURNED);
    Assert.assertTrue(actual.size() <= MAX_LISTINGS_RETURNED);

    int containsCounter = 0;
    for (Listing expectedListing : expected) {
      for (Listing actualListing : actual) {
        if (expectedListing.getName().equals(actualListing.getName())) {
          containsCounter++;
        }
      }
    }
    Assert.assertEquals(containsCounter, expected.size());
  }
}