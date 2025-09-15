# Basic Automation Framework

A comprehensive Selenium-based test automation framework built with Java, TestNG, and Maven.

## 🏗️ Framework Architecture

```
AutomationFramework/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/utils/     # Utility classes
│   │   │       ├── ConfigReader.java
│   │   │       ├── ExcelReader.java
│   │   │       ├── WaitHelper.java
│   │   │       ├── ExtentManager.java
│   │   │       ├── ScreenshotHelper.java
│   │   │       └── ExcelDataGenerator.java
│   │   └── resources/
│   │       ├── config.properties      # Configuration file
│   │       └── testdata.xlsx         # Test data file
│   └── test/
│       └── java/
│           └── org/example/
│               ├── base/             # Base classes
│               │   └── BaseTest.java
│               ├── pages/            # Page Object Model classes
│               │   └── LoginPage.java
│               └── tests/            # Test classes
│                   └── LoginTest.java
│
├── .github/workflows/
│   └── automation-tests.yml         # GitHub Actions CI/CD
├── pom.xml                          # Maven dependencies
├── testng.xml                       # TestNG configuration
├── Jenkinsfile                      # Jenkins pipeline
└── README.md                        # This file
```

## 🚀 Features

- **Cross-browser Support**: Chrome (easily extensible)
- **Parallel Execution**: TestNG parallel execution support
- **Data-Driven Testing**: Excel integration with Apache POI
- **Detailed Reporting**: ExtentReports with screenshots
- **Page Object Model**: Maintainable and scalable test structure
- **CI/CD Ready**: GitHub Actions and Jenkins pipeline
- **Configuration Management**: Centralized configuration
- **Screenshot Capture**: Automatic screenshot on test failures
- **Wait Strategies**: Comprehensive WebDriver wait utilities

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Chrome browser
- IDE (IntelliJ IDEA, Eclipse, etc.)

## 🛠️ Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd BasicAutomationFramework
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Generate Test Data
```bash
mvn compile exec:java -Dexec.mainClass="org.example.utils.ExcelDataGenerator"
```

### 4. Update Configuration
Edit `src/main/resources/config.properties` to match your application:
```properties
base.url=https://your-application-url.com
browser=chrome
headless=false
```

## 🏃‍♂️ Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Suite
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### Run Tests in Headless Mode
```bash
mvn clean test -Dheadless=true
```

### Run Specific Test Class
```bash
mvn clean test -Dtest=LoginTest
```

### Run with Custom Parameters
```bash
mvn clean test -Dbrowser=chrome -Dheadless=true -Dbase.url=https://staging.example.com
```

## 📊 Test Reporting

### ExtentReports
- Location: `test-output/ExtentReports/`
- Detailed HTML reports with screenshots
- Test execution timeline and statistics

### TestNG Reports
- Location: `target/surefire-reports/`
- XML and HTML format reports

### Screenshots
- Location: `test-output/screenshots/`
- Automatic capture on test failures

## 🔧 Configuration Options

### config.properties
```properties
# Browser settings
browser=chrome
headless=false
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Application URLs
base.url=https://example.com
staging.url=https://staging.example.com

# Test data
test.data.file=testdata.xlsx

# Reporting
report.path=test-output/ExtentReports/
screenshot.path=test-output/screenshots/

# Parallel execution
thread.count=3
parallel.mode=methods
```

### TestNG Configuration
The `testng.xml` file supports:
- Parallel execution (methods/classes/tests)
- Test grouping and categorization
- Parameter passing
- Test suite organization

## 📝 Writing Tests

### 1. Create Page Object
```java
public class LoginPage {
    private WebDriver driver;
    private WaitHelper waitHelper;
    
    @FindBy(id = "username")
    private WebElement usernameField;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.waitHelper = new WaitHelper(driver);
        PageFactory.initElements(driver, this);
    }
    
    public void enterUsername(String username) {
        waitHelper.waitForElementToBeVisible(usernameField);
        usernameField.sendKeys(username);
    }
}
```

### 2. Create Test Class
```java
public class LoginTest extends BaseTest {
    private LoginPage loginPage;
    
    @Test
    public void testValidLogin() {
        loginPage = new LoginPage(getDriver());
        loginPage.enterUsername("testuser");
        // Add assertions
    }
}
```

## 🔄 CI/CD Integration

### GitHub Actions
- Automatic test execution on push/PR
- Parallel matrix builds
- Artifact upload (reports, screenshots)
- Test result publishing

### Jenkins Pipeline
- Parameterized builds
- Environment-specific execution
- Email notifications
- Report publishing

## 📚 Best Practices

1. **Page Object Model**: Keep page elements and actions in page classes
2. **Wait Strategies**: Use explicit waits instead of Thread.sleep()
3. **Test Data**: Use external data sources (Excel, JSON)
4. **Assertions**: Use meaningful assertion messages
5. **Logging**: Add proper logging for debugging
6. **Screenshots**: Capture screenshots on failures
7. **Parallel Execution**: Design tests to run independently

## 🐛 Troubleshooting

### Common Issues

1. **ChromeDriver Issues**
   - Ensure Chrome browser is installed
   - WebDriverManager handles driver management automatically

2. **Test Data Issues**
   - Run ExcelDataGenerator to create sample data
   - Verify Excel file path in config.properties

3. **Parallel Execution Issues**
   - Ensure tests are thread-safe
   - Use ThreadLocal for WebDriver instances

4. **CI/CD Issues**
   - Set headless=true for CI environments
   - Ensure proper permissions for artifact uploads

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For questions or issues, please create an issue in the repository or contact the development team.
