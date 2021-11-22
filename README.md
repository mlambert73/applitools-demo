
# canned-demo

## Configurable Project Parameters

All configs can be found in the `testng.xml` file.

|Parameter|Purpose|      
|--|--|       
|ClassicValidation|Enables/disables the pure Selenium validation test|
|ApplitoolsValidation|Enables/disables Applitools validation|
|APIKey|Your Applitools run API key|      
|UseVisualGrid|Enables/disables UltraFast Grid execution|      
|WaitBeforeScreenshots|Configures Eyes to wait before screenshotting|      
|LocalBrowserViewport|Configures the size of the local browser viewport when using UFG. <br/>Format: *width* x *height* <br/>Example: `1024x768` |AppVersion|Sets the version of the demo app to use (1, 2, or 3)|
|RunHeadless|Enables running browser in headless mode|
|UseOften|Enables/disables the "I use this device often" feature of the demo app|      
|SessionID|Enables/disables displaying a randomly generated session ID in the demo app|      
|ModifyFont|Enable/disables turning all CSS font styles off (turns into system default Serif font)|    
|ChangeLogo|Enables a logo change that can be used to demonstrate grouping/auto maintenance|    
|ResolveContrastIssue|Enables modifying the Log In button to resolve contrast issues|    
|ReferAFriend|Enables a header content that can be used for A/B testing demonstration|    
|URL|The URL of the demo app|      
|EnableEyesLogger|Enables/disables eyes logging in the /target folder|      
|EnableVGResourceWriter|Enables/disables the VG resource writer for debugging|      
|BatchName|Sets the batch name of the execution|      
|TestName|Sets the test name|      
|AppName|Sets the app name|      
|Branch|Sets the branch name|      
|ParentBranch|Sets the parent branch|      
|VisualGridConcurrentTests|Sets UFG concurrency|      
|RunOnDesktop|Enables/disables UFG execution on desktop browsers|      
|RunOnMobile|Enables/disables UFG execution on mobile devices|      
|MobilePortrait|If RunOnMobile=true, enables/disables UFG execution on mobile devices in portrait mode|      
|MobileLandscape|If RunOnMobile=true, enables/disables UFG execution on mobile devices in landscape mode|      
|ChromeDriver|Path to local chromedriver executable|      
|GeckoDriver|Path to local geckodriver executable|  
|ExecutionBrowser|"chrome" or "firefox"|

## Demo App URL Parameters
|URL Parameter|Options|Description|Default|  
|--|--|--|--|  
|version|1;2;3|Determines which application version is loaded|1|  
|sessionid|true;false|Turns the random session ID on or off|false|  
|modifyfont|true;false|When true, defaults the font to Sans Serif|false|  
|changelogo|true;false|When true, changes the logo|false|  
|fixcontrast|true;false|When true, enhances the contrast of the Log In button|false|  
|refer|true;false|When true, enables the Refer a Friend header|false|

## Demo App Quick Links

- [Version 1](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html)
- [Version 2](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html?version=2)
- [Version 3](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html?version=3)
- [Version 3 with New Logo](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html?version=3&changelogo=true)
- [Version 3 with New Logo and Session ID](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html?version=3&changelogo=true&sessionid=true)
- [Version 3 with New Logo and Refer a Friend Header](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html?version=3&changelogo=true&refer=true)
- [Version 3 with New Logo + Contrast Fix](http://applitoolsjenkins.eastus.cloudapp.azure.com:5000/demo.html?version=3&fixcontrast=true&changelogo=true)

## Running From Jenkins
All configuration properties detailed above can be set by passing run time parameters in Jenkins. The syntax is to pass `-D<parameter name>` into the Build Goals and Options configuration.  For example, setting the value of the `APIKey` parameter: `-DAPIKey=123456`

Parameter values passed in through Jenkins (or, more accurately, the Java runtime), will supersede the values set via the configuration in the `testng.xml` file.

This can also be combined with Jenkins parameters: `-DAPIKey=$APPLITOOLS_API_KEY` where `APPLITOOLS_API_KEY` is the name of a build parameter. 