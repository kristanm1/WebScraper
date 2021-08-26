import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;

public class WebScraper {


    public static void main(String args[]) {

        String playerName = String.join(" ", args);
        System.out.println("name: " + playerName);

        System.setProperty("webdriver.gecko.driver","./geckodriver");
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");

        FirefoxBinary firefoxBinary = new FirefoxBinary();
        //firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        FirefoxDriver driver = new FirefoxDriver(firefoxOptions);


        try {
            // fetch URL
            driver.get("https://www.nba.com/players/");
            System.out.println("fetched");
            Thread.sleep(5000);

            // allow cookies
            WebElement acceptBtn = driver.findElement(By.id("onetrust-accept-btn-handler"));
            acceptBtn.click();
            System.out.println("cookies are allowed");
            Thread.sleep(3000);

            // search player
            List<WebElement> inputTags = driver.findElements(By.tagName("input"));
            for(WebElement e : inputTags) {
                if(e.getAttribute("placeholder").equals("Search Players")) {
                    e.sendKeys(playerName);
                    System.out.println("player searched");
                    Thread.sleep(3000);
                    break;
                }
            }

            // fetch player's stats URL
            WebElement tableTag = driver.findElement(By.className("players-list"));
            WebElement tableBody = tableTag.findElement(By.tagName("tbody"));
            WebElement tableRow = tableBody.findElement(By.tagName("tr"));
            WebElement aTag = tableRow.findElement(By.tagName("a"));
            String temp[] = aTag.getAttribute("href").split("/");
            //System.out.println(String.join(" ", temp));
            //System.out.println(temp[temp.length - 2]);
            driver.get("https://www.nba.com/stats/player/" + temp[temp.length - 2] + "/");
            System.out.println("fetched");
            Thread.sleep(3000);

            // select mode: Per 40 minutes
            Select selectMode = new Select(driver.findElement(By.name("PerMode")));
            selectMode.selectByVisibleText("Per 40 Minutes");
            System.out.println("selected mode");
            Thread.sleep(3000);
            
            try {
                // parse data
                WebElement statTableTag = driver.findElement(By.tagName("nba-stat-table"));
                String dataRows[] = statTableTag.getText().split("\n");
                String rowSplited[] = dataRows[0].split(" ");
                int index = 0;
                for(int i = 1; i < rowSplited.length; i++) {
                    if(rowSplited[i].equals("3PM")) {
                        index = i;
                        break;
                    }
                }

                // display data
                System.out.println();
                for(int i = 1; i < dataRows.length; i++) {
                    rowSplited = dataRows[i].split(" ");
                    System.out.println(rowSplited[0] + " " + rowSplited[index]);
                }
            } catch(Exception e) {
                System.out.println();
                System.out.println("No Data");
            }

        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("ERROR!!!");
        } finally {
            driver.close();
        }

    }

}