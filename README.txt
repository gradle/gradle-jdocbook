1. profile tasks
2. GenerateXslFoTask
3. if there is no translation defined, then should no translation/po tasks (done)
4. default project's task name "COMMON_BOOK" (done)
5. source set of default project may has some problems
6. more test cases

check out samples/mutilpleTranslations/*.gradle for the useage.
the book structure for now is fixed.
for example, mutilbooks.gradle
src
└── main
    └── docbook
        ├── devguide
        │   └── en-US
        ├── manual
        │   ├── de-DE
        │   ├── en-US
        └── quickstart
            └── en-US

and also, the current source depends on the latest jdocbook-core (build from source)