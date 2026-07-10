1.  Naming and Formatting
    1.1 Basic Rules

        Naming convention: Strictly use camelCase.

        Booleans: Must use the is prefix (e.g., isActive).

        Collection types: Prefix variable names with type indicators — use ls for arrays/lists (e.g., lsUsers), and set for sets (e.g., setPermissions).

        Indentation: Use tabs for indentation, not spaces.

        Braces: Left braces are placed at the end of the statement (1TBS style), without a line break.

1.2 Refined Rules for Special Scenarios (Based on Actual Naming Practices)

    Constants: Must use UPPER_SNAKE_CASE (e.g., MAX_ATTEMPT = 5).

    Classes/constructors: Use PascalCase; common abbreviations (e.g., Mgr, Auth) are allowed, and full spelling is not required (e.g., UserAuthMgr is acceptable).

    Event/callback handlers: Do not add on or handle prefixes; directly use verbs to describe the action (e.g., submitForm, clickSave).

    Async data-fetching functions: Uniformly use the get prefix (e.g., getUserInfo); fetch or load are prohibited.

    Private/internal properties: Do not prefix with underscores (no _ or __); use plain camelCase (e.g., localCache).

    Acronym handling: Keep acronyms fully uppercase (e.g., XML, API, ID); when multiple acronyms or acronyms combined with regular words appear, use underscores as separators for readability (e.g., parseXML_API, not parseXmlApi or parseXMLAPI).

2. Comments and Documentation

   Module-level comments: At the beginning of each module/file, use English multi-line comments to describe the core principles, design rationale, and important constants of that module.

   Inline code comments: Only for complex logic blocks, use brief English comments to explain the purpose; do not write redundant line‑by‑line comments.

3. Error Handling (By Scenario)

   General scenarios: try-catch may be used to catch exceptions; prefer to re‑throw upward (do not swallow exceptions).

   High‑performance / low‑latency scenarios: try-catch is strictly prohibited (to avoid performance overhead); instead, use error codes, precondition checks, or a Result pattern.

4. Type System

   Static typing: Mandatory (e.g., TypeScript, Python annotations, Java, etc.).

   Coverage: Unless extremely exceptional, must clearly specify complete static types for all parameters, return values, and complex object structures (any or implicit any is forbidden).

5. Testing Strategy

   Test directory: All unit test files must be placed in the tests/ folder at the project root.

   Test naming: Test functions must follow the testXXX format (describing functionality, e.g., testCalculateTotal).

6. Logging and Debugging

   Log format: In production, use plain text format; levels (ERROR / WARNING / INFO) must be clearly distinguished.

   Debugging means: During development, prefer debugger statements or IDE breakpoints; do not leave garbage output via console.log.

7. Module Imports

   Ordering rule: Arrange import statements in the actual order of use in the code; no mandatory grouping (do not distinguish between external libraries and internal modules).
