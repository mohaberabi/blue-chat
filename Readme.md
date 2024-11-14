# BlueChat

BlueChat is an Android application designed to show the usage of bluetooth pairing , scanning , and
data transfer

---

## Features

- **Device Scanning**: Discover nearby Bluetooth devices.
- **Device Pairing**: Pair and connect to available Bluetooth devices.
- **Real-Time Messaging**: Exchange messages with connected devices through a simple chat logic.
- **Foreground Services**:Keeping app alive during chat or scanning

---

## Technologies Used

- **Jetpack Libraries**: Includes ViewModel, Navigation.
- **Hilt**: For dependency injection.
- **Coroutines**: For asynchronous programming.
- **Robolectric and MockK**: For unit testing and mocking dependencies.
- **Bluetooth**

---

## Code Documentation

BlueChat is documented using **Dokka**, `dokka/html` folder after running the following
command:

```bash
./gradlew dokkaHtml