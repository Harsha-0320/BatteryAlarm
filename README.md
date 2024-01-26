## Overview-
The Battery Alarm App is an Android Mobile Application, alerting the user about mobile battery status while on charge but going off when the battery level reaches certain battery limit set by the user. It provides the user a choice to select the limit for high level battery alarm. The only condition for the app is that it has to be kept running in the background only for the alarm to go off. After clicking stop the app will stop running. It is very simple to use and easy to understand.

## Features-
- Monitors battery level and charging status.
- Allows users to set a custom threshold for the high battery alarm.
- Plays an alarm and triggers vibration when the battery level exceeds the set threshold.
- Displays battery information including level, health, voltage, type, charging source, temperature, and charging status.
- Provides alerts for both high and low battery levels.
- Allows users to stop the alarm manually through the app interface.
- Automatically terminates from the background after the user stops the alarm.

## Usage-
1. **Setting the High Battery Threshold:**
    - Open the app.
    - Use the SeekBar to set the desired high battery threshold for the alarm.

2. **Monitoring Battery Status:**
    - The app will monitor the battery level and charging status in the background.

3. **High Battery Alert:**
    - When the battery level exceeds the set threshold while charging, the app will play an alarm and trigger vibration.
    - An alert dialog will prompt the user to stop the alarm.

4. **Low Battery Alert:**
    - If the battery level drops below 15%, the app will play an alarm and trigger vibration.
    - An alert dialog will prompt the user to acknowledge the low battery condition.

5. **Stopping the Alarm:**
    - The user can manually stop the alarm by pressing the "STOP" button in the alert dialog.
   
6. **Automatic Termination:**
    - After the user stops the alarm, the app will automatically terminate from the background.

## Dependencies
- The app uses standard Android SDK libraries for battery monitoring and UI components.
- The alarm sound is included in the `res/raw` directory.

## IMAGES:

Application Interface after installing in physical device.

![physical_device.jpeg](app%2Fsrc%2Fmain%2Fres%2Fdrawable%2Fphysical_device.jpeg))

## Contributing
Contributions to the project are welcome. Feel free to open issues or submit pull requests.