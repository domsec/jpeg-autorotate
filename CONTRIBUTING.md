## Contributing to JPEG Autorotate

Before digging right into code, there are a few guidelines that we need contributors to follow so that we can have a chance of keeping on top of things.

### Getting Started

- If you're planning to implement a new feature, it's best to create an `feature request` issue first that captures the problem you want to address. You can discuss your proposed solution in that issue. This gives a chance to provide feedback to ensure you don't waste your time on something that isn't considered to be in JPEG Autorotate's scope. Once the issue has been approved and its label is changed to `feature`, you have the go ahead to start implementation.
- An easier way to help is to contribute images with different metadata sets for research and testing. It's best you create a `new image` issue first.
- If you have encountered a bug, it's best you create a `bug` issue first.
    - Clearly describe the issue including steps to reproduce it.
    - Make sure you fill in the earliest version that you know has the issue.
- [Fork](https://help.github.com/en/github/getting-started-with-github/fork-a-repo) and check out your forked repository.

### Making Changes

- Create a new `feature branch` for your isolated work.
    - Usually you should base your branch on the `master` branch.
    - A good `feature branch` name can be a shortened version of a feature or bug name, e.g. `rotate-image`.
    - If you have submitted multiple issues, try to maintain separate branches and pull requests.
- Commits
    - Make sure your commit messages are meaningful
- Respect original code style
    - Check for unnecessary whitespace with `git diff` -- check before committing.
- One Pull Request per feature
    - Send multiple *Pull Requests* if you want to do more than one thing.
- Document any changes
    - Make sure the `README.md`, source files and any other relevant documentation are kept up-to-date.
- Adding new images
    - Process the image with the library and manually check with [EXIF Info](https://exifinfo.org/) to ensure proper rotation and metadata processing.
    - Add processed image in `src/test/resources` and rename the file with prefix `result_`, e.g. `image.jpg` -> `result_image.jpg`.
- Add tests
    - Provide necessary tests for your code changes and/or image addtions, typically in `src/test/java`.
    - Make sure your changes don't break any existing tests by running `mvn test`.

### Submitting Changes

We accept contributions via Pull Requests on [Github](https://github.com/domsec/jpeg-autorotate)

- Push your changes to a `feature branch` in your fork of the repository.
- Submit a *Pull Request* to the `master` branch.
    - Verify *Files Changed* shows only your intended changes and does **not** include additional files like `target/*.class`.