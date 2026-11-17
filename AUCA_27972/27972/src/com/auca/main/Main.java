package com.auca.main;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

/**
 * AUCA_ID School Management System - console application.
 * One shared Scanner, typed in-memory collections, a smart multi-step form engine
 * (progress, BACK/CANCEL, review, edit field, save) and nested prerequisite registration.
 */
public class Main {

    // ------------------------------------------------------------------ state
    private static final Scanner SCANNER = new Scanner(System.in);

    private static final List<Department> DEPARTMENTS = new ArrayList<>();
    private static final List<Student> STUDENTS = new ArrayList<>();
    private static final List<Teacher> TEACHERS = new ArrayList<>();
    private static final List<Administrator> ADMINISTRATORS = new ArrayList<>();
    private static final List<Course> COURSES = new ArrayList<>();
    private static final List<Classroom> CLASSROOMS = new ArrayList<>();
    private static final List<Enrollment> ENROLLMENTS = new ArrayList<>();
    private static final List<Assessment> ASSESSMENTS = new ArrayList<>();
    private static final List<FeePayment> FEE_PAYMENTS = new ArrayList<>();
    private static final List<LibraryBook> LIBRARY_BOOKS = new ArrayList<>();
    private static final List<BookLoan> BOOK_LOANS = new ArrayList<>();

    private static final Object BACK = new Object();
    private static final Object CANCEL = new Object();

    private static final String[] ACADEMIC_STATUS = {"ACTIVE", "INACTIVE"};
    private static final String[] ENROLLMENT_STATUS = {"ACTIVE", "COMPLETED", "WITHDRAWN"};
    private static final String[] EMPLOYMENT_TYPES = {"FULL_TIME", "PART_TIME", "CONTRACT"};
    private static final String[] ASSESSMENT_TYPES = {"QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM", "PROJECT"};
    private static final String[] PAYMENT_METHODS = {"CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD"};
    private static final String[] YES_NO = {"YES", "NO"};
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^\\+?[0-9]{10,15}$";

    // ------------------------------------------------------- functional types
    @FunctionalInterface
    interface Parser { Object parse(String in, Object[] v) throws Exception; }

    @FunctionalInterface
    interface Check { void check(Object value, Object[] v) throws Exception; }

    @FunctionalInterface
    interface Selector { Object select(); }

    @FunctionalInterface
    interface Applier { void apply(Object value) throws Exception; }

    @FunctionalInterface
    interface Saver { Identifiable save(Object[] v) throws Exception; }

    @FunctionalInterface
    interface Source { Collection<String> get(); }

    /** One prompted attribute of a smart form. */
    static class Field {
        final int step;
        final String label;
        final String hint;
        Parser parser;
        Selector selector;
        Check check;
        Source uniqueIn;
        String uniqueWhat;
        Applier applier;
        Supplier<Object> current;

        Field(int step, String label, String hint) {
            this.step = step;
            this.label = label;
            this.hint = hint;
        }

        /** Asks until a valid value, BACK or CANCEL is returned. Invalid input only re-prompts this field. */
        Object ask(int no, Object[] v) {
            while (true) {
                Object result;
                if (selector != null) {
                    System.out.println(String.format("%02d  %-22s [%s]", no, label, hint));
                    result = selector.select();
                } else {
                    String in = readLine(String.format("%02d  %-22s [%s] : ", no, label, hint)).trim();
                    if (in.equalsIgnoreCase("CANCEL")) {
                        return CANCEL;
                    }
                    if (in.equalsIgnoreCase("BACK")) {
                        return BACK;
                    }
                    try {
                        result = parser.parse(in, v);
                    } catch (Exception e) {
                        System.out.println("! " + e.getMessage());
                        System.out.println("  Please re-enter field " + String.format("%02d", no) + ".");
                        continue;
                    }
                }
                if (result == CANCEL || result == BACK) {
                    return result;
                }
                if (check != null) {
                    try {
                        check.check(result, v);
                    } catch (Exception e) {
                        System.out.println("! " + e.getMessage());
                        if (selector != null) {
                            continue;
                        }
                        System.out.println("  Please re-enter field " + String.format("%02d", no) + ".");
                        continue;
                    }
                }
                return result;
            }
        }
    }

    // ------------------------------------------------------------------- main
    public static void main(String[] args) {
        System.out.println("Welcome to AUCA_ID - School Management System");
        boolean running = true;
        while (running) {
            showMenu();
            try {
                String input = readLine("Select an option: ").trim();
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> flow("Department", Main::registerDepartment);
                    case 2 -> flow("Student", Main::registerStudent);
                    case 3 -> flow("Teacher", Main::registerTeacher);
                    case 4 -> flow("Administrator", Main::registerAdministrator);
                    case 5 -> flow("Course", Main::registerCourse);
                    case 6 -> flow("Classroom", Main::registerClassroom);
                    case 7 -> flow("Enrollment", Main::enrollStudent);
                    case 8 -> flow("Assessment", Main::recordAssessment);
                    case 9 -> flow("Fee payment", Main::recordFeePayment);
                    case 10 -> flow("Library book", Main::registerLibraryBook);
                    case 11 -> flow("Book loan", Main::borrowBook);
                    case 12 -> returnBook();
                    case 13 -> viewAllRecords();
                    case 14 -> searchRecordById();
                    case 15 -> updateRecord();
                    case 16 -> showStudentAcademicSummary();
                    case 17 -> showPaymentHistory();
                    case 18 -> showAvailableBooks();
                    case 19 -> demonstratePolymorphism();
                    case 0 -> {
                        System.out.println("Exiting AUCA_ID. Goodbye.");
                        running = false;
                    }
                    default -> System.out.println("! Invalid selection. Choose a number from 0 to 19.");
                }
            } catch (NumberFormatException e) {
                System.out.println("! Invalid input. Enter a menu number from 0 to 19.");
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
                running = false;
            } catch (Exception e) {
                System.out.println("! Unexpected problem: " + e.getMessage() + " - returning to the main menu.");
            }
        }
    }

    private static void showMenu() {
        System.out.println();
        System.out.println("============== AUCA_ID SCHOOL MANAGEMENT ==============");
        System.out.println(" 1. Register Department       11. Borrow Library Book");
        System.out.println(" 2. Register Student          12. Return Library Book");
        System.out.println(" 3. Register Teacher          13. View All Records");
        System.out.println(" 4. Register Administrator    14. Search Record by ID");
        System.out.println(" 5. Register Course           15. Update Existing Record");
        System.out.println(" 6. Register Classroom        16. Student Academic Summary");
        System.out.println(" 7. Enroll Student            17. Student Payment History");
        System.out.println(" 8. Record Assessment         18. View Available Books");
        System.out.println(" 9. Record Fee Payment        19. Demonstrate Polymorphism");
        System.out.println("10. Register Library Book      0. Exit");
        System.out.println("=======================================================");
    }

    // ------------------------------------------------------------ input utils
    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!SCANNER.hasNextLine()) {
            throw new IllegalStateException("\nInput stream closed. Exiting application.");
        }
        return SCANNER.nextLine();
    }

    private static int readChoice(String prompt, int min, int max) {
        while (true) {
            String in = readLine(prompt).trim();
            try {
                int n = Integer.parseInt(in);
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // fall through to the message below
            }
            System.out.println("! Enter a number from " + min + " to " + max + ".");
        }
    }

    private static boolean confirm(String prompt) {
        while (true) {
            String a = readLine(prompt).trim().toLowerCase();
            if (a.equals("y") || a.equals("yes")) {
                return true;
            }
            if (a.equals("n") || a.equals("no")) {
                return false;
            }
            System.out.println("! Please answer Y or N.");
        }
    }

    private static String show(Object o) {
        if (o == null) {
            return "(none)";
        }
        if (o instanceof Boolean b) {
            return b ? "YES" : "NO";
        }
        if (o instanceof Identifiable i) {
            return i.getDisplayName();
        }
        return o.toString();
    }

    // ---------------------------------------------------------------- parsers
    private static Parser text(int min, int max) {
        return (s, v) -> {
            String t = s.trim();
            if (t.isEmpty()) {
                throw new ValidationException("This value cannot be blank.");
            }
            if (t.length() < min || t.length() > max) {
                throw new ValidationException("Length must be between " + min + " and " + max + " characters.");
            }
            return t;
        };
    }

    private static Parser name() {
        Parser base = text(2, 60);
        return (s, v) -> {
            String t = (String) base.parse(s, v);
            if (!t.matches("^[\\p{L} .'-]+$")) {
                throw new ValidationException("Names may contain only letters, spaces, apostrophes, dots and hyphens.");
            }
            return t;
        };
    }

    private static Parser email() {
        return (s, v) -> {
            String t = s.trim();
            if (t.length() > Registrable.MAX_EMAIL_LENGTH) {
                throw new ValidationException("Email may have at most " + Registrable.MAX_EMAIL_LENGTH + " characters.");
            }
            if (!t.matches(EMAIL_REGEX)) {
                throw new ValidationException("Invalid email syntax (example: alice@example.com).");
            }
            return t;
        };
    }

    private static Parser phone() {
        return (s, v) -> {
            String t = s.trim();
            if (!t.matches(PHONE_REGEX)) {
                throw new ValidationException("Phone must contain 10-15 digits with an optional leading +.");
            }
            return t;
        };
    }

    private static LocalDate parseDate(String s) throws ValidationException {
        try {
            return LocalDate.parse(s.trim());
        } catch (DateTimeParseException e) {
            throw new ValidationException("Invalid date: use YYYY-MM-DD with a real calendar date (example: 2004-05-16).");
        }
    }

    private static Parser dateNotFuture() {
        return (s, v) -> {
            LocalDate d = parseDate(s);
            if (d.isAfter(LocalDate.now())) {
                throw new ValidationException("Date cannot be in the future.");
            }
            return d;
        };
    }

    private static Parser dateOfBirth() {
        return (s, v) -> {
            LocalDate d = parseDate(s);
            if (!d.isBefore(LocalDate.now())) {
                throw new ValidationException("Date of birth must be in the past.");
            }
            return d;
        };
    }

    private static Parser dateAfterField(int idx, String what) {
        return (s, v) -> {
            LocalDate d = parseDate(s);
            LocalDate base = (LocalDate) v[idx];
            if (base != null && !d.isAfter(base)) {
                throw new ValidationException("Date must be after the " + what + " (" + base + ").");
            }
            return d;
        };
    }

    private static Parser dateNotBefore(LocalDate base, String what) {
        return (s, v) -> {
            LocalDate d = parseDate(s);
            if (d.isBefore(base)) {
                throw new ValidationException("Date cannot be before the " + what + " (" + base + ").");
            }
            if (d.isAfter(LocalDate.now())) {
                throw new ValidationException("Date cannot be in the future.");
            }
            return d;
        };
    }

    private static Parser intRange(int min, int max) {
        return (s, v) -> {
            try {
                int n = Integer.parseInt(s.trim());
                if (n < min || n > max) {
                    throw new ValidationException("Value must be between " + min + " and " + max + ".");
                }
                return n;
            } catch (NumberFormatException e) {
                throw new ValidationException("Enter a whole number between " + min + " and " + max + ".");
            }
        };
    }

    private static Parser copiesAtMost(int totalIdx) {
        return (s, v) -> {
            int n;
            try {
                n = Integer.parseInt(s.trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("Enter a whole number (0 or more).");
            }
            int total = (Integer) v[totalIdx];
            if (n < 0 || n > total) {
                throw new ValidationException("Available copies must be between 0 and total copies (" + total + ").");
            }
            return n;
        };
    }

    private static Parser money() {
        return (s, v) -> {
            try {
                BigDecimal b = new BigDecimal(s.trim());
                if (b.signum() <= 0) {
                    throw new ValidationException("Amount must be strictly positive.");
                }
                if (b.scale() > 2) {
                    throw new ValidationException("Use at most 2 decimal places.");
                }
                return b;
            } catch (NumberFormatException e) {
                throw new ValidationException("Enter a valid monetary amount (example: 150000.50).");
            }
        };
    }

    private static Parser positiveDouble() {
        return (s, v) -> {
            try {
                double d = Double.parseDouble(s.trim());
                if (!Double.isFinite(d) || d <= 0) {
                    throw new ValidationException("Value must be greater than zero.");
                }
                return d;
            } catch (NumberFormatException e) {
                throw new ValidationException("Enter a valid number greater than zero.");
            }
        };
    }

    private static Parser scoreAtMost(int maxIdx) {
        return (s, v) -> {
            try {
                double d = Double.parseDouble(s.trim());
                double max = (Double) v[maxIdx];
                if (!Double.isFinite(d) || d < 0 || d > max) {
                    throw new ValidationException("Obtained score must be between 0 and " + max + ".");
                }
                return d;
            } catch (NumberFormatException e) {
                throw new ValidationException("Enter a valid number.");
            }
        };
    }

    private static Parser choice(String... options) {
        return (s, v) -> {
            String t = s.trim();
            try {
                int n = Integer.parseInt(t);
                if (n >= 1 && n <= options.length) {
                    return options[n - 1];
                }
            } catch (NumberFormatException e) {
                for (String o : options) {
                    if (o.equalsIgnoreCase(t.replace(' ', '_'))) {
                        return o;
                    }
                }
            }
            throw new ValidationException("Choose one of: " + choiceHint(options) + ".");
        };
    }

    private static Parser yesNo() {
        Parser c = choice(YES_NO);
        return (s, v) -> "YES".equals(c.parse(s, v));
    }

    private static String choiceHint(String... options) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < options.length; i++) {
            sb.append(i == 0 ? "" : " / ").append(i + 1).append(' ').append(options[i]);
        }
        return sb.toString();
    }

    private static Parser term() {
        return (s, v) -> {
            String t = s.trim().toUpperCase().replaceAll("\\s+", " ");
            if (!t.matches("^(FALL|SPRING|SUMMER) [0-9]{4}$")) {
                throw new ValidationException("Semester must look like 'FALL 2026', 'SPRING 2027' or 'SUMMER 2027'.");
            }
            return t;
        };
    }

    private static Parser unique(Parser base, Source existing, String what, Supplier<String> self) {
        return (s, v) -> {
            Object r = base.parse(s, v);
            String own = self == null ? null : self.get();
            for (String e : existing.get()) {
                if (e != null && e.equalsIgnoreCase(String.valueOf(r)) && !(own != null && own.equalsIgnoreCase(e))) {
                    throw new DuplicateRecordException(what + " '" + r + "' already exists.");
                }
            }
            return r;
        };
    }

    // ----------------------------------------------------------- field makers
    private static Field fld(int step, String label, String hint, Parser p) {
        Field f = new Field(step, label, hint);
        f.parser = p;
        return f;
    }

    private static Field ufld(int step, String label, String hint, Parser base, Source existing, String what) {
        Field f = fld(step, label, hint, unique(base, existing, what, null));
        f.uniqueIn = existing;
        f.uniqueWhat = what;
        return f;
    }

    private static Field sfld(int step, String label, Selector s) {
        Field f = new Field(step, label, "select");
        f.selector = s;
        return f;
    }

    private static Field withCheck(Field f, Check c) {
        f.check = c;
        return f;
    }

    private static Field efld(String label, String hint, Parser p, Supplier<Object> cur, Applier ap) {
        Field f = new Field(0, label, hint);
        f.parser = p;
        f.current = cur;
        f.applier = ap;
        return f;
    }

    // ------------------------------------------------------------ unique data
    private static List<String> personIds() {
        List<String> l = new ArrayList<>();
        STUDENTS.forEach(p -> l.add(p.getPersonId()));
        TEACHERS.forEach(p -> l.add(p.getPersonId()));
        ADMINISTRATORS.forEach(p -> l.add(p.getPersonId()));
        return l;
    }

    private static List<String> memberNumbers() {
        List<String> l = new ArrayList<>();
        STUDENTS.forEach(p -> l.add(p.getMemberNumber()));
        TEACHERS.forEach(p -> l.add(p.getMemberNumber()));
        return l;
    }

    private static List<String> allEmails() {
        List<String> l = new ArrayList<>();
        STUDENTS.forEach(p -> {
            l.add(p.getEmail());
            l.add(p.getInstitutionalEmail());
        });
        TEACHERS.forEach(p -> {
            l.add(p.getEmail());
            l.add(p.getInstitutionalEmail());
        });
        ADMINISTRATORS.forEach(p -> l.add(p.getEmail()));
        return l;
    }

    private static <T> List<String> col(List<T> list, Function<T, String> f) {
        List<String> l = new ArrayList<>();
        list.forEach(x -> l.add(f.apply(x)));
        return l;
    }

    // --------------------------------------------------------------- selector
    private static Selector sel(String noun, Supplier<List<?>> items, Function<Object, String> label,
                                Supplier<Object> register, boolean optional, String emptyMsg) {
        return () -> {
            while (true) {
                List<?> list = items.get();
                if (list.isEmpty()) {
                    if (optional) {
                        System.out.println("  No " + noun + " exists yet - left unassigned (it can be assigned later).");
                        return null;
                    }
                    System.out.println(emptyMsg != null ? emptyMsg : "No " + noun + " exists. Register a " + noun + " before continuing.");
                    if (register == null) {
                        return CANCEL;
                    }
                    System.out.println("  1 Register prerequisite now   2 Return to menu");
                    String in = readLine("  Select: ").trim();
                    if (in.equalsIgnoreCase("BACK")) {
                        return BACK;
                    }
                    if (in.equals("1")) {
                        Object created = register.get();
                        if (created != null) {
                            return created;
                        }
                        System.out.println("  Prerequisite was not registered.");
                        continue;
                    }
                    if (in.equals("2") || in.equalsIgnoreCase("CANCEL")) {
                        return CANCEL;
                    }
                    System.out.println("! Choose 1 or 2.");
                    continue;
                }
                System.out.println("  +----------------- CHOOSE " + noun.toUpperCase() + " -----------------");
                for (int i = 0; i < list.size(); i++) {
                    System.out.println("  | " + (i + 1) + "  " + label.apply(list.get(i)));
                }
                int addNo = list.size() + 1;
                int noneNo = register != null ? list.size() + 2 : list.size() + 1;
                if (register != null) {
                    System.out.println("  | " + addNo + "  [+] Register a new " + noun);
                }
                if (optional) {
                    System.out.println("  | " + noneNo + "  [-] No " + noun + " (leave unassigned)");
                }
                System.out.println("  | 0  Cancel");
                System.out.println("  +--------------------------------------------------");
                String in = readLine("  Select option (or BACK): ").trim();
                if (in.equalsIgnoreCase("BACK")) {
                    return BACK;
                }
                int n;
                try {
                    n = Integer.parseInt(in);
                } catch (NumberFormatException e) {
                    System.out.println("! Enter one of the numbers shown.");
                    continue;
                }
                if (n == 0) {
                    return CANCEL;
                }
                if (n >= 1 && n <= list.size()) {
                    return list.get(n - 1);
                }
                if (register != null && n == addNo) {
                    Object created = register.get();
                    if (created != null) {
                        return created;
                    }
                    System.out.println("  Nothing was registered; choose again.");
                    continue;
                }
                if (optional && n == noneNo) {
                    return null;
                }
                System.out.println("! Option " + n + " does not exist.");
            }
        };
    }

    private static Selector deptSel() {
        return sel("Department", () -> DEPARTMENTS, o -> ((Department) o).getDisplayName(), Main::registerDepartment, false, null);
    }

    private static Selector teacherSel(boolean optional) {
        return sel("Teacher", () -> TEACHERS,
                o -> ((Teacher) o).getDisplayName() + " - " + ((Teacher) o).getSpecialization(),
                optional ? null : Main::registerTeacher, optional, null);
    }

    private static Selector studentSel() {
        return sel("Student", () -> STUDENTS,
                o -> ((Student) o).getDisplayName() + " [" + ((Student) o).getStudentNumber() + "]",
                Main::registerStudent, false, null);
    }

    private static Selector courseSel() {
        return sel("Course", () -> COURSES,
                o -> "[" + ((Course) o).getCourseId() + "] " + ((Course) o).getCourseCode() + " - " + ((Course) o).getCourseTitle(),
                Main::registerCourse, false, null);
    }

    private static String enrollmentLabel(Enrollment e) {
        return "[" + e.getEnrollmentId() + "] " + e.getStudent().getDisplayName() + " -> " + e.getCourse().getCourseCode()
                + " (" + e.getSemester() + ", " + e.getEnrollmentStatus() + ")";
    }

    private static String bookLabel(LibraryBook b) {
        return "[" + b.getBookId() + "] " + b.getTitle() + " by " + b.getAuthor() + " (Available: " + b.getAvailableCopies() + "/" + b.getTotalCopies() + ")";
    }

    // --------------------------------------------------------- the form engine
    /**
     * Runs a guided multi-step form: step headers, BACK/CANCEL, full review, Save / Edit field / Cancel.
     * Nothing is stored until Save passes every validation. Returns the saved record or null when cancelled.
     */
    private static Identifiable runForm(String noun, String[] steps, List<Field> fields, Saver saver) {
        int n = fields.size();
        Object[] vals = new Object[n];
        int total = steps.length + 1;
        System.out.println();
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| AUCA_ID  /  SCHOOL MANAGEMENT");
        System.out.println(String.format("| %-30s %24s", noun.toUpperCase() + " REGISTRATION", "Steps: " + total));
        System.out.println("+----------------------------------------------------------+");
        for (int i = 0; i < steps.length; i++) {
            System.out.println("| [" + (i + 1) + "] " + steps[i]);
        }
        System.out.println("| [" + total + "] Review & confirm");
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| Enter the requested value. Type BACK to return;");
        System.out.println("| CANCEL to discard the unsaved record.");
        System.out.println("+----------------------------------------------------------+");

        int i = 0;
        int shownStep = -1;
        while (i < n) {
            Field fd = fields.get(i);
            if (fd.step != shownStep) {
                System.out.println();
                System.out.println("[STEP " + (fd.step + 1) + "/" + total + "] " + steps[fd.step].toUpperCase());
                shownStep = fd.step;
            }
            Object r = fd.ask(i + 1, vals);
            if (r == CANCEL) {
                System.out.println("Registration cancelled. Nothing was saved.");
                return null;
            }
            if (r == BACK) {
                if (i == 0) {
                    System.out.println("  Already at the first field. Type CANCEL to discard.");
                } else {
                    i--;
                    shownStep = -1;
                }
                continue;
            }
            vals[i] = r;
            i++;
        }

        while (true) {
            printReview(noun, steps, fields, vals, total);
            int c = readChoice("Select option: ", 0, 2);
            if (c == 0) {
                System.out.println("Registration cancelled. Nothing was saved.");
                return null;
            }
            if (c == 2) {
                int no = readChoice("Field number to edit (1-" + n + ", 0 = back to review): ", 0, n);
                if (no == 0) {
                    continue;
                }
                Field fd = fields.get(no - 1);
                System.out.println("Current value: " + show(vals[no - 1]) + "   (type BACK or CANCEL to keep it)");
                Object r = fd.ask(no, vals);
                if (r != BACK && r != CANCEL) {
                    vals[no - 1] = r;
                    System.out.println("Field " + String.format("%02d", no) + " updated.");
                }
                continue;
            }
            try {
                for (int k = 0; k < n; k++) {
                    Field fd = fields.get(k);
                    if (fd.uniqueIn != null) {
                        for (String e : fd.uniqueIn.get()) {
                            if (e != null && e.equalsIgnoreCase(String.valueOf(vals[k]))) {
                                throw new DuplicateRecordException(fd.uniqueWhat + " '" + vals[k] + "' already exists. Edit field " + (k + 1) + ".");
                            }
                        }
                    }
                }
                Identifiable saved = saver.save(vals);
                System.out.println("+ " + noun + " " + saved.getId() + " registered successfully.");
                return saved;
            } catch (Exception e) {
                System.out.println("! Cannot save: " + e.getMessage());
                System.out.println("  Use 'Edit field' to correct it, or Cancel. Nothing has been saved.");
            }
        }
    }

    private static void printReview(String noun, String[] steps, List<Field> fields, Object[] vals, int total) {
        System.out.println();
        System.out.println("+--------------- REVIEW " + noun.toUpperCase() + " (Step " + total + "/" + total + ") ---------------+");
        int shown = -1;
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            if (f.step != shown) {
                System.out.println("| " + steps[f.step].toUpperCase());
                shown = f.step;
            }
            System.out.println(String.format("| %02d %-22s : %s", i + 1, f.label, show(vals[i])));
        }
        System.out.println("+----------------------------------------------------------+");
        System.out.println("| [1] Save   [2] Edit field   [0] Cancel");
        System.out.println("+----------------------------------------------------------+");
    }

    /** Wraps a registration so the user can register another record, view it, or return to the menu. */
    private static void flow(String noun, Supplier<Identifiable> register) {
        while (true) {
            Identifiable saved = register.get();
            if (saved == null) {
                return;
            }
            System.out.println("  [1] Register another   [2] View " + noun.toLowerCase() + "   [0] Main menu");
            int c = readChoice("Select: ", 0, 2);
            if (c == 2) {
                ((Registrable) saved).printDetails();
                System.out.println("  " + ((Registrable) saved).getRegistrationSummary());
                return;
            }
            if (c == 0) {
                return;
            }
        }
    }

    // ------------------------------------------------------ shared field sets
    private static void personFields(List<Field> f, int step) {
        f.add(ufld(step, "Person ID", "String | unique | 4-30 chars", text(Identifiable.MIN_ID_LENGTH, Identifiable.MAX_ID_LENGTH), Main::personIds, "Person ID"));
        f.add(fld(step, "First name", "String | 2-60 chars", name()));
        f.add(fld(step, "Last name", "String | 2-60 chars", name()));
        f.add(ufld(step, "Email", "String | unique | email", email(), Main::allEmails, "Email"));
        f.add(fld(step, "Phone", "String | 10-15 digits", phone()));
        f.add(fld(step, "Date of birth", "LocalDate | YYYY-MM-DD", dateOfBirth()));
    }

    private static void academicFields(List<Field> f, int step) {
        f.add(ufld(step, "Member number", "String | unique | 4-30", text(4, 30), Main::memberNumbers, "Member number"));
        f.add(sfld(step, "Department", deptSel()));
        f.add(fld(step, "Join date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(step, "Academic status", choiceHint(ACADEMIC_STATUS), choice(ACADEMIC_STATUS)));
        f.add(ufld(step, "Institutional email", "String | unique | email", email(), Main::allEmails, "Institutional email"));
        f.add(fld(step, "Advisor name", "String | 2-120 chars", text(2, 120)));
    }

    // ------------------------------------------------------------ registration
    private static Identifiable registerDepartment() {
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Department ID", "String | unique | 3-30 chars", text(3, 30), () -> col(DEPARTMENTS, Department::getDepartmentId), "Department ID"));
        f.add(fld(0, "Department name", "String | 2-60 chars", text(2, 60)));
        f.add(ufld(0, "Department code", "String | unique | 2-20 chars", text(2, 20), () -> col(DEPARTMENTS, Department::getDepartmentCode), "Department code"));
        f.add(fld(0, "Office location", "String | 2-120 chars", text(2, 120)));
        f.add(fld(0, "Contact email", "String | email", email()));
        f.add(sfld(1, "Head teacher", teacherSel(true)));
        return runForm("Department", new String[]{"Department details", "Head teacher (optional)"}, f, v -> {
            Department d = new Department((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4], (Teacher) v[5]);
            d.validate();
            DEPARTMENTS.add(d);
            return d;
        });
    }

    private static Identifiable registerStudent() {
        List<Field> f = new ArrayList<>();
        personFields(f, 0);
        academicFields(f, 1);
        f.add(ufld(2, "Student number", "String | unique | 4-30", text(4, 30), () -> col(STUDENTS, Student::getStudentNumber), "Student number"));
        f.add(fld(2, "Program name", "String | 2-120 chars", text(2, 120)));
        f.add(fld(2, "Admission date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(2, "Study year", "int | 1-6", intRange(1, 6)));
        f.add(fld(2, "Guardian phone", "String | 10-15 digits", phone()));
        f.add(fld(2, "Scholarship", choiceHint(YES_NO), yesNo()));
        return runForm("Student", new String[]{"Personal details", "Academic details", "Student details"}, f, v -> {
            Student s = new Student((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4], (LocalDate) v[5],
                    (String) v[6], (Department) v[7], (LocalDate) v[8], (String) v[9], (String) v[10], (String) v[11],
                    (String) v[12], (String) v[13], (LocalDate) v[14], (Integer) v[15], (String) v[16], (Boolean) v[17]);
            s.validate();
            STUDENTS.add(s);
            return s;
        });
    }

    private static Identifiable registerTeacher() {
        List<Field> f = new ArrayList<>();
        personFields(f, 0);
        academicFields(f, 1);
        f.add(ufld(2, "Employee number", "String | unique | 4-30", text(4, 30), () -> col(TEACHERS, Teacher::getEmployeeNumber), "Employee number"));
        f.add(fld(2, "Specialization", "String | 2-120 chars", text(2, 120)));
        f.add(fld(2, "Hire date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(2, "Salary", "BigDecimal | > 0", money()));
        f.add(fld(2, "Office number", "String | 1-30 chars", text(1, 30)));
        f.add(fld(2, "Employment type", choiceHint(EMPLOYMENT_TYPES), choice(EMPLOYMENT_TYPES)));
        return runForm("Teacher", new String[]{"Personal details", "Academic details", "Teacher details"}, f, v -> {
            Teacher t = new Teacher((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4], (LocalDate) v[5],
                    (String) v[6], (Department) v[7], (LocalDate) v[8], (String) v[9], (String) v[10], (String) v[11],
                    (String) v[12], (String) v[13], (LocalDate) v[14], (BigDecimal) v[15], (String) v[16], (String) v[17]);
            t.validate();
            TEACHERS.add(t);
            return t;
        });
    }

    private static Identifiable registerAdministrator() {
        List<Field> f = new ArrayList<>();
        personFields(f, 0);
        f.add(ufld(1, "Staff number", "String | unique | 4-30", text(4, 30), () -> col(ADMINISTRATORS, Administrator::getStaffNumber), "Staff number"));
        f.add(fld(1, "Job title", "String | 2-120 chars", text(2, 120)));
        f.add(fld(1, "Assigned office", "String | 2-60 chars", text(2, 60)));
        f.add(fld(1, "Employment date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(1, "Access level", "int | 1-5", intRange(1, 5)));
        f.add(fld(1, "Active", choiceHint(YES_NO), yesNo()));
        return runForm("Administrator", new String[]{"Personal details", "Administrator details"}, f, v -> {
            Administrator a = new Administrator((String) v[0], (String) v[1], (String) v[2], (String) v[3], (String) v[4], (LocalDate) v[5],
                    (String) v[6], (String) v[7], (String) v[8], (LocalDate) v[9], (Integer) v[10], (Boolean) v[11]);
            a.validate();
            ADMINISTRATORS.add(a);
            return a;
        });
    }

    private static Identifiable registerCourse() {
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Course ID", "String | unique | 3-30", text(3, 30), () -> col(COURSES, Course::getCourseId), "Course ID"));
        f.add(ufld(0, "Course code", "String | unique | 2-20", text(2, 20), () -> col(COURSES, Course::getCourseCode), "Course code"));
        f.add(fld(0, "Course title", "String | 2-120 chars", text(2, 120)));
        f.add(fld(0, "Credit hours", "int | 1-6", intRange(1, 6)));
        f.add(sfld(1, "Department", deptSel()));
        f.add(sfld(1, "Assigned teacher", teacherSel(false)));
        return runForm("Course", new String[]{"Course details", "Department & teacher"}, f, v -> {
            Course c = new Course((String) v[0], (String) v[1], (String) v[2], (Integer) v[3], (Department) v[4], (Teacher) v[5]);
            c.validate();
            COURSES.add(c);
            return c;
        });
    }

    private static Identifiable registerClassroom() {
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Classroom ID", "String | unique | 2-30", text(2, 30), () -> col(CLASSROOMS, Classroom::getClassroomId), "Classroom ID"));
        f.add(fld(0, "Building name", "String | 2-60 chars", text(2, 60)));
        f.add(fld(0, "Room number", "String | 1-20 chars", text(1, 20)));
        f.add(fld(0, "Capacity", "int | > 0", intRange(1, 100000)));
        f.add(fld(0, "Floor number", "int | >= 0", intRange(0, 200)));
        f.add(fld(0, "Available", choiceHint(YES_NO), yesNo()));
        return runForm("Classroom", new String[]{"Classroom details"}, f, v -> {
            Classroom c = new Classroom((String) v[0], (String) v[1], (String) v[2], (Integer) v[3], (Integer) v[4], (Boolean) v[5]);
            c.validate();
            CLASSROOMS.add(c);
            return c;
        });
    }

    private static boolean activeEnrollmentExists(Student s, Course c, String semester) {
        for (Enrollment e : ENROLLMENTS) {
            if (e.getStudent() == s && e.getCourse() == c && e.getSemester().equalsIgnoreCase(semester)
                    && "ACTIVE".equalsIgnoreCase(e.getEnrollmentStatus())) {
                return true;
            }
        }
        return false;
    }

    private static Identifiable enrollStudent() {
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Enrollment ID", "String | unique | 3-30", text(3, 30), () -> col(ENROLLMENTS, Enrollment::getEnrollmentId), "Enrollment ID"));
        f.add(sfld(0, "Student", studentSel()));
        f.add(sfld(0, "Course", courseSel()));
        f.add(fld(1, "Enrollment date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(1, "Semester", "e.g. FALL 2026 | SPRING | SUMMER", term()));
        f.add(withCheck(fld(1, "Enrollment status", choiceHint(ENROLLMENT_STATUS), choice(ENROLLMENT_STATUS)), (val, v) -> {
            if ("ACTIVE".equals(val) && activeEnrollmentExists((Student) v[1], (Course) v[2], (String) v[4])) {
                throw new DuplicateRecordException("An active enrollment already exists for this student, course and semester.");
            }
        }));
        return runForm("Enrollment", new String[]{"Student & course", "Enrollment details"}, f, v -> {
            if ("ACTIVE".equals(v[5]) && activeEnrollmentExists((Student) v[1], (Course) v[2], (String) v[4])) {
                throw new DuplicateRecordException("An active enrollment already exists for this student, course and semester.");
            }
            Enrollment e = new Enrollment((String) v[0], (Student) v[1], (Course) v[2], (LocalDate) v[3], (String) v[4], (String) v[5]);
            e.validate();
            ENROLLMENTS.add(e);
            return e;
        });
    }

    private static Identifiable recordAssessment() {
        Selector enrollmentSel = sel("Enrollment", () -> ENROLLMENTS, o -> enrollmentLabel((Enrollment) o), Main::enrollStudent, false,
                "No enrollment exists. Enroll a student before recording an assessment.");
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Assessment ID", "String | unique | 3-30", text(3, 30), () -> col(ASSESSMENTS, Assessment::getAssessmentId), "Assessment ID"));
        f.add(withCheck(sfld(0, "Enrollment", enrollmentSel), (val, v) -> {
            if (!"ACTIVE".equalsIgnoreCase(((Enrollment) val).getEnrollmentStatus())) {
                throw new PrerequisiteMissingException("Only an ACTIVE enrollment can be assessed. Choose another enrollment.");
            }
        }));
        f.add(fld(1, "Assessment type", choiceHint(ASSESSMENT_TYPES), choice(ASSESSMENT_TYPES)));
        f.add(fld(1, "Maximum score", "double | > 0", positiveDouble()));
        f.add(fld(1, "Obtained score", "double | 0..maximum", scoreAtMost(3)));
        f.add(fld(1, "Assessment date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        return runForm("Assessment", new String[]{"Enrollment", "Assessment details"}, f, v -> {
            Assessment a = new Assessment((String) v[0], (Enrollment) v[1], (String) v[2], (Double) v[3], (Double) v[4], (LocalDate) v[5]);
            a.validate();
            ASSESSMENTS.add(a);
            return a;
        });
    }

    private static Identifiable recordFeePayment() {
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Payment ID", "String | unique | 3-30", text(3, 30), () -> col(FEE_PAYMENTS, FeePayment::getPaymentId), "Payment ID"));
        f.add(sfld(0, "Student", studentSel()));
        f.add(fld(1, "Amount", "BigDecimal | > 0 | 2 decimals", money()));
        f.add(fld(1, "Payment date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(1, "Payment method", choiceHint(PAYMENT_METHODS), choice(PAYMENT_METHODS)));
        f.add(ufld(1, "Receipt number", "String | unique | 3-30", text(3, 30), () -> col(FEE_PAYMENTS, FeePayment::getReceiptNumber), "Receipt number"));
        return runForm("Fee payment", new String[]{"Student", "Payment details"}, f, v -> {
            FeePayment p = new FeePayment((String) v[0], (Student) v[1], (BigDecimal) v[2], (LocalDate) v[3], (String) v[4], (String) v[5]);
            p.validate();
            FEE_PAYMENTS.add(p);
            return p;
        });
    }

    private static Identifiable registerLibraryBook() {
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Book ID", "String | unique | 3-30", text(3, 30), () -> col(LIBRARY_BOOKS, LibraryBook::getBookId), "Book ID"));
        f.add(ufld(0, "ISBN", "String | unique | 10-17 chars", text(10, 17), () -> col(LIBRARY_BOOKS, LibraryBook::getIsbn), "ISBN"));
        f.add(fld(0, "Title", "String | 2-120 chars", text(2, 120)));
        f.add(fld(0, "Author", "String | 2-120 chars", text(2, 120)));
        f.add(fld(1, "Total copies", "int | >= 0", intRange(0, 100000)));
        f.add(fld(1, "Available copies", "int | 0..total", copiesAtMost(4)));
        return runForm("Library book", new String[]{"Book details", "Stock"}, f, v -> {
            LibraryBook b = new LibraryBook((String) v[0], (String) v[1], (String) v[2], (String) v[3], (Integer) v[4], (Integer) v[5]);
            b.validate();
            LIBRARY_BOOKS.add(b);
            return b;
        });
    }

    private static Identifiable borrowBook() {
        Selector bookSel = sel("Library book", () -> LIBRARY_BOOKS, o -> bookLabel((LibraryBook) o), Main::registerLibraryBook, false, null);
        List<Field> f = new ArrayList<>();
        f.add(ufld(0, "Loan ID", "String | unique | 3-30", text(3, 30), () -> col(BOOK_LOANS, BookLoan::getLoanId), "Loan ID"));
        f.add(sfld(0, "Student", studentSel()));
        f.add(withCheck(sfld(0, "Library book", bookSel), (val, v) -> {
            if (((LibraryBook) val).getAvailableCopies() <= 0) {
                throw new BookUnavailableException("No copies of '" + ((LibraryBook) val).getTitle() + "' are available. Choose another book.");
            }
        }));
        f.add(fld(1, "Borrowed date", "LocalDate | YYYY-MM-DD", dateNotFuture()));
        f.add(fld(1, "Due date", "LocalDate | after borrowed", dateAfterField(3, "borrowed date")));
        return runForm("Book loan", new String[]{"Student & book", "Loan dates"}, f, v -> {
            LibraryBook book = (LibraryBook) v[2];
            if (book.getAvailableCopies() <= 0) {
                throw new BookUnavailableException("No copies of '" + book.getTitle() + "' are available.");
            }
            BookLoan loan = new BookLoan((String) v[0], (Student) v[1], book, (LocalDate) v[3], (LocalDate) v[4], null);
            loan.validate();
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            BOOK_LOANS.add(loan);
            return loan;
        });
    }

    private static void returnBook() {
        Selector loanSel = () -> {
            List<BookLoan> active = new ArrayList<>();
            BOOK_LOANS.forEach(l -> {
                if (l.getReturnedDate() == null) {
                    active.add(l);
                }
            });
            if (active.isEmpty()) {
                System.out.println("No active book loan exists. Borrow a book first.");
                return CANCEL;
            }
            return sel("Active loan", () -> active,
                    o -> "[" + ((BookLoan) o).getLoanId() + "] " + ((BookLoan) o).getStudent().getDisplayName() + " - "
                            + ((BookLoan) o).getBook().getTitle() + " (due " + ((BookLoan) o).getDueDate() + ")",
                    null, false, null).select();
        };
        List<Field> f = new ArrayList<>();
        f.add(sfld(0, "Loan", loanSel));
        Field rd = new Field(0, "Return date", "LocalDate | YYYY-MM-DD");
        rd.parser = (s, v) -> dateNotBefore(((BookLoan) v[0]).getBorrowedDate(), "borrowed date").parse(s, v);
        f.add(rd);
        Identifiable done = runForm("Book return", new String[]{"Return details"}, f, v -> {
            BookLoan loan = (BookLoan) v[0];
            if (loan.getReturnedDate() != null) {
                throw new ValidationException("This loan has already been returned.");
            }
            LibraryBook book = loan.getBook();
            if (book.getAvailableCopies() + 1 > book.getTotalCopies()) {
                throw new ValidationException("Stock error: available copies would exceed total copies.");
            }
            loan.setReturnedDate((LocalDate) v[1]);
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            return loan;
        });
        if (done != null) {
            System.out.println("  Stock updated: " + bookLabel(((BookLoan) done).getBook()));
        }
    }

    // ------------------------------------------------------------- reporting
    private static Map<String, List<?>> sections() {
        Map<String, List<?>> m = new LinkedHashMap<>();
        m.put("Departments", DEPARTMENTS);
        m.put("Students", STUDENTS);
        m.put("Teachers", TEACHERS);
        m.put("Administrators", ADMINISTRATORS);
        m.put("Courses", COURSES);
        m.put("Classrooms", CLASSROOMS);
        m.put("Enrollments", ENROLLMENTS);
        m.put("Assessments", ASSESSMENTS);
        m.put("Fee payments", FEE_PAYMENTS);
        m.put("Library books", LIBRARY_BOOKS);
        m.put("Book loans", BOOK_LOANS);
        return m;
    }

    private static void viewAllRecords() {
        System.out.println("=================== ALL RECORDS ===================");
        int total = 0;
        for (Map.Entry<String, List<?>> e : sections().entrySet()) {
            System.out.println(e.getKey() + " (" + e.getValue().size() + "):");
            if (e.getValue().isEmpty()) {
                System.out.println("   (no records)");
            }
            for (Object o : e.getValue()) {
                System.out.println("   - " + ((Registrable) o).getRegistrationSummary());
                total++;
            }
        }
        System.out.println("Total records: " + total);
    }

    private static List<String> keysOf(Object o) {
        List<String> k = new ArrayList<>();
        k.add(((Identifiable) o).getId());
        if (o instanceof Student s) {
            k.add(s.getStudentNumber());
            k.add(s.getMemberNumber());
        } else if (o instanceof Teacher t) {
            k.add(t.getEmployeeNumber());
            k.add(t.getMemberNumber());
        } else if (o instanceof Administrator a) {
            k.add(a.getStaffNumber());
        } else if (o instanceof Department d) {
            k.add(d.getDepartmentCode());
        } else if (o instanceof Course c) {
            k.add(c.getCourseCode());
        } else if (o instanceof LibraryBook b) {
            k.add(b.getIsbn());
        } else if (o instanceof FeePayment p) {
            k.add(p.getReceiptNumber());
        }
        return k;
    }

    private static List<Object> findRecords(String key) {
        List<Object> found = new ArrayList<>();
        for (List<?> l : sections().values()) {
            for (Object o : l) {
                for (String k : keysOf(o)) {
                    if (k != null && k.equalsIgnoreCase(key)) {
                        found.add(o);
                        break;
                    }
                }
            }
        }
        return found;
    }

    private static void searchRecordById() {
        String id = readLine("Enter record ID / code (0 = back): ").trim();
        if (id.isEmpty() || id.equals("0")) {
            return;
        }
        try {
            List<Object> found = findRecords(id);
            if (found.isEmpty()) {
                throw new RecordNotFoundException("No record found with ID: " + id);
            }
            for (Object o : found) {
                System.out.println("[" + o.getClass().getSimpleName() + "]");
                ((Registrable) o).printDetails();
                System.out.println("   " + ((Registrable) o).getRegistrationSummary());
            }
        } catch (RecordNotFoundException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    // ----------------------------------------------------------------- update
    private static List<Field> personEdits(Person p) {
        List<Field> l = new ArrayList<>();
        l.add(efld("First name", "String | 2-60", name(), p::getFirstName, v -> p.setFirstName((String) v)));
        l.add(efld("Last name", "String | 2-60", name(), p::getLastName, v -> p.setLastName((String) v)));
        l.add(efld("Email", "String | unique | email", unique(email(), Main::allEmails, "Email", p::getEmail), p::getEmail, v -> p.setEmail((String) v)));
        l.add(efld("Phone", "String | 10-15 digits", phone(), p::getPhoneNumber, v -> p.setPhoneNumber((String) v)));
        return l;
    }

    private static List<Field> editFields(Object rec) {
        List<Field> l = new ArrayList<>();
        if (rec instanceof Student s) {
            l.addAll(personEdits(s));
            l.add(efld("Academic status", choiceHint(ACADEMIC_STATUS), choice(ACADEMIC_STATUS), s::getAcademicStatus, v -> s.setAcademicStatus((String) v)));
            l.add(efld("Advisor name", "String | 2-120", text(2, 120), s::getAdvisorName, v -> s.setAdvisorName((String) v)));
            l.add(efld("Program name", "String | 2-120", text(2, 120), s::getProgramName, v -> s.setProgramName((String) v)));
            l.add(efld("Study year", "int | 1-6", intRange(1, 6), s::getStudyYear, v -> s.setStudyYear((Integer) v)));
            l.add(efld("Guardian phone", "String | 10-15 digits", phone(), s::getGuardianPhone, v -> s.setGuardianPhone((String) v)));
            l.add(efld("Scholarship", choiceHint(YES_NO), yesNo(), s::isScholarshipStatus, v -> s.setScholarshipStatus((Boolean) v)));
        } else if (rec instanceof Teacher t) {
            l.addAll(personEdits(t));
            l.add(efld("Academic status", choiceHint(ACADEMIC_STATUS), choice(ACADEMIC_STATUS), t::getAcademicStatus, v -> t.setAcademicStatus((String) v)));
            l.add(efld("Specialization", "String | 2-120", text(2, 120), t::getSpecialization, v -> t.setSpecialization((String) v)));
            l.add(efld("Salary", "BigDecimal | > 0", money(), t::getSalary, v -> t.setSalary((BigDecimal) v)));
            l.add(efld("Office number", "String | 1-30", text(1, 30), t::getOfficeNumber, v -> t.setOfficeNumber((String) v)));
            l.add(efld("Employment type", choiceHint(EMPLOYMENT_TYPES), choice(EMPLOYMENT_TYPES), t::getEmploymentType, v -> t.setEmploymentType((String) v)));
        } else if (rec instanceof Administrator a) {
            l.addAll(personEdits(a));
            l.add(efld("Job title", "String | 2-120", text(2, 120), a::getJobTitle, v -> a.setJobTitle((String) v)));
            l.add(efld("Assigned office", "String | 2-60", text(2, 60), a::getAssignedOffice, v -> a.setAssignedOffice((String) v)));
            l.add(efld("Access level", "int | 1-5", intRange(1, 5), a::getAccessLevel, v -> a.setAccessLevel((Integer) v)));
            l.add(efld("Active", choiceHint(YES_NO), yesNo(), a::isActive, v -> a.setActive((Boolean) v)));
        } else if (rec instanceof Department d) {
            l.add(efld("Department name", "String | 2-60", text(2, 60), d::getDepartmentName, v -> d.setDepartmentName((String) v)));
            l.add(efld("Office location", "String | 2-120", text(2, 120), d::getOfficeLocation, v -> d.setOfficeLocation((String) v)));
            l.add(efld("Contact email", "String | email", email(), d::getContactEmail, v -> d.setContactEmail((String) v)));
            Field head = new Field(0, "Head teacher", "select");
            head.selector = teacherSel(true);
            head.current = d::getHeadTeacher;
            head.applier = v -> d.setHeadTeacher((Teacher) v);
            l.add(head);
        } else if (rec instanceof Course c) {
            l.add(efld("Course title", "String | 2-120", text(2, 120), c::getCourseTitle, v -> c.setCourseTitle((String) v)));
            l.add(efld("Credit hours", "int | 1-6", intRange(1, 6), c::getCreditHours, v -> c.setCreditHours((Integer) v)));
        } else if (rec instanceof Classroom c) {
            l.add(efld("Capacity", "int | > 0", intRange(1, 100000), c::getCapacity, v -> c.setCapacity((Integer) v)));
            l.add(efld("Floor number", "int | >= 0", intRange(0, 200), c::getFloorNumber, v -> c.setFloorNumber((Integer) v)));
            l.add(efld("Available", choiceHint(YES_NO), yesNo(), c::isAvailable, v -> c.setAvailable((Boolean) v)));
        } else if (rec instanceof Enrollment e) {
            l.add(efld("Enrollment status", choiceHint(ENROLLMENT_STATUS), choice(ENROLLMENT_STATUS), e::getEnrollmentStatus, v -> e.setEnrollmentStatus((String) v)));
        } else if (rec instanceof Assessment a) {
            l.add(efld("Obtained score", "double | 0.." + a.getMaximumScore(),
                    (s, v) -> {
                        double d = Double.parseDouble(s.trim());
                        if (d < 0 || d > a.getMaximumScore()) {
                            throw new ValidationException("Obtained score must be between 0 and " + a.getMaximumScore() + ".");
                        }
                        return d;
                    }, a::getObtainedScore, v -> a.setObtainedScore((Double) v)));
        } else if (rec instanceof FeePayment p) {
            l.add(efld("Payment method", choiceHint(PAYMENT_METHODS), choice(PAYMENT_METHODS), p::getPaymentMethod, v -> p.setPaymentMethod((String) v)));
        } else if (rec instanceof LibraryBook b) {
            l.add(efld("Title", "String | 2-120", text(2, 120), b::getTitle, v -> b.setTitle((String) v)));
            l.add(efld("Author", "String | 2-120", text(2, 120), b::getAuthor, v -> b.setAuthor((String) v)));
            l.add(efld("Total copies", "int | >= available", intRange(0, 100000), b::getTotalCopies, v -> b.setTotalCopies((Integer) v)));
        }
        return l;
    }

    private static void updateRecord() {
        String key = readLine("Enter ID of the record to update (0 = back): ").trim();
        if (key.isEmpty() || key.equals("0")) {
            return;
        }
        List<Object> found = findRecords(key);
        if (found.isEmpty()) {
            System.out.println("! No record found with ID: " + key);
            return;
        }
        Object rec = found.get(0);
        List<Field> fs = editFields(rec);
        if (fs.isEmpty()) {
            System.out.println("This record type has no editable fields (borrow/return books through options 11-12).");
            return;
        }
        while (true) {
            System.out.println();
            System.out.println("------ UPDATE " + rec.getClass().getSimpleName().toUpperCase() + " (IDs cannot be changed) ------");
            ((Registrable) rec).printDetails();
            for (int i = 0; i < fs.size(); i++) {
                System.out.println(String.format(" %2d. %-20s : %s", i + 1, fs.get(i).label, show(fs.get(i).current.get())));
            }
            System.out.println("  0. Done");
            int n = readChoice("Select field number to edit: ", 0, fs.size());
            if (n == 0) {
                return;
            }
            Field fd = fs.get(n - 1);
            System.out.println("Current value: " + show(fd.current.get()) + "   (BACK/CANCEL keeps it)");
            Object r = fd.ask(n, new Object[0]);
            if (r == BACK || r == CANCEL) {
                System.out.println("No change made.");
                continue;
            }
            if (!confirm("Change " + fd.label + " from '" + show(fd.current.get()) + "' to '" + show(r) + "'? (Y/N): ")) {
                System.out.println("Change discarded.");
                continue;
            }
            try {
                fd.applier.apply(r);
                System.out.println("+ " + fd.label + " updated successfully.");
            } catch (Exception e) {
                System.out.println("! Update rejected: " + e.getMessage());
            }
        }
    }

    private static void showStudentAcademicSummary() {
        Object chosen = sel("Student", () -> STUDENTS, o -> ((Student) o).getDisplayName(), null, false,
                "No student exists. Register a student first (option 2).").select();
        if (chosen == CANCEL || chosen == BACK || chosen == null) {
            return;
        }
        Student s = (Student) chosen;
        System.out.println("=========== ACADEMIC SUMMARY ===========");
        System.out.println("Student : " + s.getDisplayName() + " | Program: " + s.getProgramName() + " | Year " + s.getStudyYear()
                + " | Dept: " + s.getDepartment().getDepartmentName());
        int credits = 0;
        int count = 0;
        for (Enrollment e : ENROLLMENTS) {
            if (e.getStudent() != s) {
                continue;
            }
            count++;
            System.out.println("- " + e.getCourse().getCourseCode() + " " + e.getCourse().getCourseTitle() + " | "
                    + e.getSemester() + " | " + e.getEnrollmentStatus() + " | " + e.getCourse().getCreditHours() + " credits");
            if ("ACTIVE".equalsIgnoreCase(e.getEnrollmentStatus()) || "COMPLETED".equalsIgnoreCase(e.getEnrollmentStatus())) {
                credits += e.getCourse().getCreditHours();
            }
            double sum = 0;
            int k = 0;
            for (Assessment a : ASSESSMENTS) {
                if (a.getEnrollment() == e) {
                    double pct = a.getObtainedScore() * 100.0 / a.getMaximumScore();
                    System.out.println(String.format("     * %s: %.1f/%.1f (%.1f%%) on %s", a.getAssessmentType(), a.getObtainedScore(),
                            a.getMaximumScore(), pct, a.getAssessmentDate()));
                    sum += pct;
                    k++;
                }
            }
            if (k > 0) {
                System.out.println(String.format("     Average: %.1f%%", sum / k));
            }
        }
        if (count == 0) {
            System.out.println("(no enrollments yet)");
        }
        System.out.println("Credit hours (active + completed): " + credits);
    }

    private static void showPaymentHistory() {
        Object chosen = sel("Student", () -> STUDENTS, o -> ((Student) o).getDisplayName(), null, false,
                "No student exists. Register a student first (option 2).").select();
        if (chosen == CANCEL || chosen == BACK || chosen == null) {
            return;
        }
        Student s = (Student) chosen;
        System.out.println("=========== PAYMENT HISTORY: " + s.getDisplayName() + " ===========");
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (FeePayment p : FEE_PAYMENTS) {
            if (p.getStudent() == s) {
                System.out.println("- " + p.getPaymentDate() + " | " + p.getAmount() + " | " + p.getPaymentMethod() + " | Receipt " + p.getReceiptNumber());
                total = total.add(p.getAmount());
                count++;
            }
        }
        if (count == 0) {
            System.out.println("(no payments recorded)");
        }
        System.out.println("Total paid: " + total);
    }

    private static void showAvailableBooks() {
        if (LIBRARY_BOOKS.isEmpty()) {
            System.out.println("No library books are registered. Register one with option 10.");
            return;
        }
        int shown = 0;
        System.out.println("=========== AVAILABLE BOOKS ===========");
        for (LibraryBook b : LIBRARY_BOOKS) {
            if (b.getAvailableCopies() > 0) {
                System.out.println("- " + bookLabel(b));
                shown++;
            }
        }
        if (shown == 0) {
            System.out.println("All registered books are currently on loan.");
        }
    }

    private static void demonstratePolymorphism() {
        System.out.println("=========== POLYMORPHISM DEMONSTRATION ===========");
        System.out.println("Person and AcademicMember are abstract: they declare abstract methods (getRoleDescription,");
        System.out.println("getAcademicCategory) that have no body, so 'new Person(...)' would be incomplete and is rejected");
        System.out.println("by the compiler. Only concrete subclasses (Student, Teacher, Administrator) can be instantiated.");
        System.out.println("Constructors chain upward with super(...): Student -> AcademicMember -> Person.");
        System.out.println();
        List<Person> people = new ArrayList<>();
        people.addAll(STUDENTS);
        people.addAll(TEACHERS);
        people.addAll(ADMINISTRATORS);
        System.out.println("-- List<Person> (runtime polymorphism, overridden methods) --");
        if (people.isEmpty()) {
            System.out.println("   (no Person records yet - register students/teachers/administrators first)");
        }
        for (Person p : people) {
            String category = (p instanceof AcademicMember am) ? " | category: " + am.getAcademicCategory() : "";
            System.out.println("   " + p.getRoleDescription() + " -> " + p.getDisplayName() + category);
            p.printDetails();
        }
        List<Identifiable> all = new ArrayList<>();
        for (List<?> l : sections().values()) {
            for (Object o : l) {
                all.add((Identifiable) o);
            }
        }
        System.out.println("-- List<Identifiable> (interface polymorphism over unrelated classes) --");
        if (all.isEmpty()) {
            System.out.println("   (no records yet)");
        }
        for (Identifiable i : all) {
            System.out.println("   " + i.getClass().getSimpleName() + " | " + i.getId() + " | " + i.getDisplayName() + " | active=" + i.isActive());
        }
    }
}
