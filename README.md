[![jcenter](https://api.bintray.com/packages/syex/Rode/Rode/images/download.svg)](https://bintray.com/syex/Rode/Rode/_latestVersion)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)

# Rode 
Rode is an Android MVP library, that provides stateful presenters that survive configuration changes with the help of 
[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html).

Rode is fully written in Kotlin and aims to be as lightweight as possible. Unlike other MVP libraries, your Activities or Fragments
don't need to extend anything from Rode.

# Download
Rode is available on jcenter:
```groovy
dependencies {
    implementation "de.syex:rode:$rodeVersion"
}
```

Your project needs to target at least support library version 26.1.0 in order to work with Rode.

A dependency to `android.arch.lifecycle:extensions` will be added to your project automatically. It contains Android's 
[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel.html) and util classes.

# Usage
First, let your presenter extend `RodePresenter`

```kotlin
class HelloWorldPresenter : RodePresenter<HelloWordView>() {
}
```

Second, make your `Activity` or `Fragment` implement `RodePresenterProvider`
```kotlin
class MainActivity : AppCompatActivity(),
        HelloWordView, 
        RodePresenterProvider<HelloWorldPresenter, HelloWordView> {
        
    // Called one time to create a single surviving instance of HelloWorldPresenter
    override fun createPresenter(): HelloWorldPresenter {
        return HelloWorldPresenter()
    }
}
```

Third, get your presenter!
```kotlin
private lateinit var presenter: HelloWorldPresenter

override fun onCreate(savedInstanceState: Bundle?) {
   super.onCreate(savedInstanceState)
   setContentView(R.layout.activity_main)
    
   presenter = ViewModelProviders.of(this)
               .get(RodeViewModel::class.java)
               .providePresenter(this)
}
```

Your `presenter` will be the same instance every time you call it (every time a new view is created).

# What does Rode do for me?
 * Your presenter survives any configuration changes. Rode will keep your presenter alive until your Activity or Fragment finishes resp. 
 [ViewModel.onCleared()](https://developer.android.com/reference/android/arch/lifecycle/ViewModel.html#onCleared()) is called.
 * Rode automatically attaches and detaches your view to/from your presenter. Rode does that by observing the 
 [Lifecycle](https://developer.android.com/reference/android/arch/lifecycle/Lifecycle.html) of your Activity/Fragment.
 You can implement following methods in your presenter to handle any lifecycle changes:
 ```kotlin
 override fun onCreate() {
  // called right after the presenter has been created
 }

override fun onDestroy() {
  // called when the presenter is going to die 
}

override fun onViewAttached(view: HelloWordView) {
  // called when a view has been attached to the presenter
}

override fun onViewDetached() {
  // called when the view has been detached from the presenter
}
 ```
 
 `onViewAttached(view)` and `onViewDetached()` correspond to 
 [Lifecycle.Event.ON_START](https://developer.android.com/reference/android/arch/lifecycle/Lifecycle.Event.html) and 
  [Lifecycle.Event.ON_STOP](https://developer.android.com/reference/android/arch/lifecycle/Lifecycle.Event.html)
  
* Rode can store your view commands until a view is attached, so you don't need to handle it yourself
```kotlin
override fun onCreate() {
  sendToView(ViewCommand { it.setHelloWorldText("Hello World from the presenter") })
}
```
In `RodePresenter.onCreate()` obviously there is no view, yet. The `ViewCommand` will be stored until there is one and then
be executed. If you call it while there is a view it will be the same as directly calling the method on the view.

* Are you annoyed having to store the error states of inputs to set an error on a `TextInputLayout` again if the user
rotates the device?
Rode keeps all  `ViewCommands` you sent in history and automatically executes them again on a new view!

```kotlin
fun validateInput(input: String) {
  if (!isValid(input)) {
    sendToView(ViewCommand { it.setInputError() })
  }
}
```

`setInputError()` will be automatically sent to a new view that attaches.

If you also have a method like `clearInputError()` it would be stupid to send `setInputError()` followed by `clearInputError()`
to a new view. For that purpose, you can give a `ViewCommand` a `tag`
```kotlin
fun validateInput(input: String) {
  if (!isValid(input)) {
    sendToView(viewCommand = ViewCommand { it.setInputError() }, tag = "inputError")
  }
  else {
    sendToView(viewCommand = ViewCommand { it.clearInputError() }, tag = "inputError")
  }
}
```

Rode will send your `ViewCommand` to the view, once, but afterward erase both commands from history and won't send any of
them to a new view.

If you don't need that feature, you can of course still directly call your `view` from your presenter.
Also there's `sendToViewOnce(viewCommand)` to not keep that command in history, but have the advantage of not caring if a view is
 currently attached to the presenter.

# Testing
If you use the methods `sendToView()` or `sendToViewOnce()` your unit tests will fail, as they're designed to run on the 
UI thread. To handle that, there's `RodePresenter.test()`. It returns an instance of `RodeTestUtils` and does three things
upon initialization:

* Changes the thread your `ViewCommands` are executed on to the current, default thread.
* Calls `RodePresenter.onCreate()`.
* Calls `RodePresenter.onViewAttached(view)`.

Further it has three methods you can use to test your presenter:

* `onLifecycleStart(view)` - Has the same effect as if the presenter would receive an `ON_START` event. It attaches a new view and
calls `onViewAttached(view)`
* `onLifecycleStop()` - Has the same effect as if the presenter would receive an `ON_STOP` event. It detaches the view and
calls `onViewDetached()`
* `onLifecycleDestroy()` - Has the same effect as if the presenter would receive a `ViewModel.onCleared()` event. It calls `onDestroy()`

# How does Rode work?
TODO

# License
```
The MIT License (MIT)

Copyright (c) 2017 Tom Seifert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
