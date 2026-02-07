<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a id="readme-top"></a>

<!-- PROJECT SHIELDS -->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Unlicense License][license-shield]][license-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <h3 align="center">🛡️ GitGuard</h3>

  <p align="center">
    A natural language CLI that translates your intent into safe Git commands using GitHub Copilot.
    <br />
    <a href="https://github.com/yorkydev/GitGuard"><strong>Explore the repo »</strong></a>
    <br />
    <br />
    <a href="https://github.com/yorkydev/GitGuard/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/yorkydev/GitGuard/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#key-features">Key Features</a></li>
        <li><a href="#our-differentiator">Our Differentiator</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#how-it-works">How It Works</a></li>
    <li><a href="#safety-guarantees">Safety Guarantees</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

**GitGuard** is a command-line interface (CLI) that bridges the gap between developers and the command line by translating natural language intentions into safe, executable Git commands. Instead of remembering complex Git syntax or worrying about dangerous operations, you simply describe what you want to do—and GitGuard suggests the right command, analyzes its safety, and asks for confirmation before executing.

### Key Features

* **🗣️ Natural Language Intent**: Simply describe what you want to do (e.g., "undo last commit but keep my files")
* **🤖 GitHub Copilot Integration**: Leverages GitHub Copilot CLI (`gh copilot`) to translate intent into accurate Git commands
* **⚠️ Risk Classification**: Automatically analyzes commands and classifies them as HIGH or LOW risk based on Git command syntax
* **📚 Educational Explanations**: Each command comes with an explanation so you learn what's happening
* **✅ Confirmation Required**: Never executes a command without explicit user approval
* **🛡️ Psychological Safety**: Built with developer confidence in mind—no more fear of destructive Git operations

### Our Differentiator

GitGuard prioritizes **Psychological Safety** for developers. We believe that a developer who feels safe experimenting with Git will:
- Learn faster
- Make fewer mistakes
- Take ownership of their version control workflow
- Contribute more confidently to team projects

Every interaction with GitGuard is designed to:
- **Explain** what the command does
- **Warn** about potential data loss
- **Empower** the developer to make informed decisions

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

GitGuard is built with modern, reliable technologies:

* [![Kotlin][Kotlin.lang]][Kotlin-url] - Modern JVM language with expressive syntax
* [![GitHub CLI][GitHub-CLI]][GitHub-CLI-url] - Official GitHub command-line tool
* [![Mordant][Mordant]][Mordant-url] - Beautiful terminal output for Kotlin
* [![Gradle][Gradle]][Gradle-url] - Robust build automation

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->
## Getting Started

### Prerequisites

Before you can use GitGuard, ensure you have the following installed:

1. **Java 21 or higher**
   - Download from [https://adoptopenjdk.net/](https://adoptopenjdk.net/) or use your package manager
   - Verify: `java --version`

2. **Git 2.20 or higher**
   - Download from [https://git-scm.com/](https://git-scm.com/)
   - Verify: `git --version`

3. **GitHub CLI (gh)** 
   - Install from [https://cli.github.com/](https://cli.github.com/)
   - Verify: `gh --version`

4. **GitHub CLI Copilot Extension**
   - Install with: `gh extension install github/gh-copilot`
   - Verify: `gh copilot --version`

### Installation

#### Option 1: Pre-built Binary (Recommended)

1. Download the latest release from [GitHub Releases](https://github.com/yorkydev/GitGuard/releases)
2. Extract the archive to your preferred location
3. Add the directory to your `PATH` environment variable
4. Verify installation: `gitguard --help`

#### Option 2: Build from Source

1. Clone the repository
   ```sh
   git clone https://github.com/yorkydev/GitGuard.git
   cd GitGuard
   ```

2. Build the project using Gradle
   ```sh
   ./gradlew build
   ```

3. Create the executable distribution
   ```sh
   ./gradlew distribution
   ```

4. The executable will be available in `build/scripts/gitguard` (Linux/macOS) or `build/scripts/gitguard.bat` (Windows)

5. Add the script directory to your `PATH` or move it to a directory already in your `PATH`

#### Verify Installation

```sh
gitguard --help
```

Should display usage information and examples.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

GitGuard is simple to use: describe your intent in natural language, and it handles the rest.

### Basic Usage

```sh
gitguard <your intention in natural language>
```

### Examples

**Undo the last commit but keep your changes:**
```sh
gitguard undo last commit but keep files
```

**Discard all uncommitted changes:**
```sh
gitguard discard all changes
```

**Go back to a previous commit:**
```sh
gitguard go back to previous commit
```

**Create and switch to a new branch:**
```sh
gitguard create new branch for feature-x
```

**Force push to remote:**
```sh
gitguard force push to origin
```

**Reset to main branch:**
```sh
gitguard reset to main branch
```

### Workflow

Each GitGuard invocation follows this workflow:

1. **Display Header** - Shows GitGuard is active
2. **Validate Repository** - Confirms you're in a Git repository
3. **Analyze Intent** - Sends your intent to GitHub Copilot CLI
4. **Classify Risk** - Evaluates if the suggested command is HIGH or LOW risk
5. **Show Analysis** - Displays the command, explanation, and risk level
6. **Ask for Confirmation** - Waits for your explicit approval
7. **Execute** - Runs the command (only if you confirmed)

### Understanding Risk Levels

#### 🔴 HIGH RISK
Operations that can cause **permanent data loss**:
- Hard resets (`git reset --hard`)
- Force pushes (`git push -f`, `git push --force`)
- Discarding all changes (`git checkout .`)
- Deleting branches (`git branch -D`)
- Cleaning untracked files (`git clean -fd`)

**These always require confirmation.**

#### 🟢 LOW RISK
Safe operations that are easy to undo:
- Creating branches (`git checkout -b`)
- Viewing history (`git log`)
- Staging changes (`git add`)
- Committing (`git commit`)
- Pulling updates (`git pull`)

**While still requiring confirmation, these are generally safe.**

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- HOW IT WORKS -->
## How It Works

GitGuard operates as a coordinated system of specialized components:

### Architecture Overview

```
User Intent
    ↓
┌─────────────────────────────────────┐
│  CommandProcessor (Orchestrator)    │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  GitValidator (Repository Check)    │
│  - Validates Git repo exists        │
│  - Retrieves branch info            │
│  - Detects uncommitted changes      │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  CopilotService (AI Suggestion)     │
│  - Sends intent to gh copilot       │
│  - Receives suggested command       │
│  - Validates command format         │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  RiskClassifier (Safety Analysis)   │
│  - Analyzes Git command syntax      │
│  - Identifies destructive patterns  │
│  - Classifies risk level            │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  Terminal (User Interface)          │
│  - Displays analysis & warnings     │
│  - Requests confirmation            │
│  - Shows beautiful output           │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│  GitExecutor (Command Execution)    │
│  - Executes approved command        │
│  - Captures output and exit codes   │
│  - Displays results to user         │
└─────────────────────────────────────┘
    ↓
Result to User
```

### Risk Classification Engine

The `RiskClassifier` is **language-agnostic**: it analyzes actual Git command syntax, not natural language. This ensures consistent risk detection regardless of language used to interact with Copilot.

**Example Classification:**

```
User Input: "eliminar todos los cambios" (Spanish)
    ↓
Copilot Suggests: git checkout .
    ↓
RiskClassifier Analysis:
  - Pattern detected: "checkout ."
  - Severity: CRITICAL
  - Reason: "Discards all uncommitted changes"
  - Result: 🔴 HIGH RISK
```

### Key Components

| Component | Responsibility | Language-Agnostic |
|-----------|-----------------|-------------------|
| **CommandProcessor** | Orchestrates the entire workflow | ✅ Yes (uses command syntax) |
| **GitValidator** | Checks Git repo status | ✅ Yes (uses Git internals) |
| **CopilotService** | Interfaces with gh copilot | ✅ Yes (receives English commands) |
| **RiskClassifier** | Evaluates command safety | ✅ Yes (analyzes Git syntax) |
| **Terminal** | Displays UI and prompts | ❌ Currently English only |
| **GitExecutor** | Executes Git commands | ✅ Yes (uses Git internals) |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- SAFETY GUARANTEES -->
## Safety Guarantees

GitGuard provides multiple layers of safety:

### 1. Repository Validation
- Verifies you're in a Git repository before proceeding
- Detects uncommitted changes and warns you
- Displays current branch context

### 2. Intelligent Risk Analysis
- Analyzes command syntax (not user input) for destructive patterns
- Identifies 30+ high-risk command patterns
- Accounts for variants and flag combinations

### 3. Always Ask First
- **Every command** requires explicit user confirmation
- Shows the full command before execution
- Displays risk assessment and explanations

### 4. Educational Feedback
- Explains what each command does
- Provides context about why a command is risky
- Helps developers understand Git operations

### 5. Atomic Operations
- Commands execute as-is (no modification)
- Exit codes are captured and reported
- Output is displayed for verification

### Patterns Detected as HIGH RISK

GitGuard detects and flags operations including:

**Destructive Reset Operations**
- `git reset --hard`
- `git reset --hard HEAD~N`

**Discard Changes**
- `git checkout .`
- `git checkout -- <file>`
- `git restore --worktree`

**Force Push (Data Loss Risk)**
- `git push -f`
- `git push --force`
- `git push --force-with-lease`

**Cleanup/Delete Operations**
- `git clean -f`
- `git clean -fd`
- `git branch -D`
- `git branch -f`

**Rebase Operations (Advanced)**
- `git rebase -i`
- `git rebase --force-with-lease`

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ROADMAP -->
## Roadmap

### Version 1.0 (Current) ✅
- [x] Natural language intent parsing
- [x] GitHub Copilot integration
- [x] Risk classification engine
- [x] Repository validation
- [x] Command execution with confirmation
- [x] Educational explanations

### Version 1.1 (Planned)
- [ ] Multi-language support (Spanish, Portuguese, French, German)
- [ ] Configuration file for custom risk rules
- [ ] Detailed command history and logging
- [ ] Integration with Git hooks

### Version 2.0 (Future)
- [ ] Interactive mode with command suggestions
- [ ] Offline risk classification (fallback mode)
- [ ] Command templates and aliases
- [ ] Analytics on safe vs risky commands attempted
- [ ] IDE plugin integration (VS Code, IntelliJ)
- [ ] GitHub Actions integration for CI/CD pipelines
- [ ] Team-level command approval workflows

### Known Limitations
- Requires GitHub CLI and Copilot extension (GitHub Copilot CLI seat required)
- Terminal output currently in English only
- Risk classification based on command syntax, not semantic analysis

See the [open issues](https://github.com/yorkydev/GitGuard/issues) for a full list of proposed features and known issues.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

We appreciate contributions! Whether it's bug reports, feature requests, or code improvements, your input helps make GitGuard better.

### How to Contribute

1. **Fork the Repository**
   ```sh
   git clone https://github.com/yorkydev/GitGuard.git
   ```

2. **Create Your Feature Branch**
   ```sh
   git checkout -b feature/AmazingFeature
   ```

3. **Make Your Changes**
   - Follow Kotlin coding conventions
   - Add tests for new functionality
   - Update documentation as needed

4. **Commit Your Changes**
   ```sh
   git commit -m 'Add some AmazingFeature'
   ```

5. **Push to Your Branch**
   ```sh
   git push origin feature/AmazingFeature
   ```

6. **Open a Pull Request**
   - Describe your changes clearly
   - Reference any related issues
   - Include before/after examples if applicable

### Development Setup

```sh
# Clone and navigate
git clone https://github.com/yorkydev/GitGuard.git
cd GitGuard

# Build the project
./gradlew build

# Run tests
./gradlew test

# Create distribution
./gradlew distribution

# Run GitGuard locally
./build/scripts/gitguard <intent>
```

### Code Style

- Use Kotlin idioms and best practices
- Maintain comprehensive kdoc comments
- Keep functions focused and single-responsibility
- Write descriptive commit messages

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## License

Distributed under the Unlicense License. This means the software is free and open to the public domain.

See `LICENSE.txt` for more information.

For more details, visit [unlicense.org](https://unlicense.org/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

**GitGuard Project**
- GitHub: [@yorkydev](https://github.com/yorkydev)
- Issues: [GitHub Issues](https://github.com/yorkydev/GitGuard/issues)

Project Repository: [https://github.com/yorkydev/GitGuard](https://github.com/yorkydev/GitGuard)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/yorkydev/GitGuard.svg?style=for-the-badge
[contributors-url]: https://github.com/yorkydev/GitGuard/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/yorkydev/GitGuard.svg?style=for-the-badge
[forks-url]: https://github.com/yorkydev/GitGuard/network/members
[stars-shield]: https://img.shields.io/github/stars/yorkydev/GitGuard.svg?style=for-the-badge
[stars-url]: https://github.com/yorkydev/GitGuard/stargazers
[issues-shield]: https://img.shields.io/github/issues/yorkydev/GitGuard.svg?style=for-the-badge
[issues-url]: https://github.com/yorkydev/GitGuard/issues
[license-shield]: https://img.shields.io/github/license/yorkydev/GitGuard.svg?style=for-the-badge
[license-url]: https://github.com/yorkydev/GitGuard/blob/master/LICENSE.txt
[Kotlin.lang]: https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white
[Kotlin-url]: https://kotlinlang.org/
[GitHub-CLI]: https://img.shields.io/badge/GitHub%20CLI-181717?style=for-the-badge&logo=github&logoColor=white
[GitHub-CLI-url]: https://cli.github.com/
[Mordant]: https://img.shields.io/badge/Mordant-FF6B6B?style=for-the-badge&logo=kotlin&logoColor=white
[Mordant-url]: https://github.com/ajalt/mordant
[Gradle]: https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white
[Gradle-url]: https://gradle.org/

