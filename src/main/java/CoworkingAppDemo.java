import java.time.LocalDateTime;
import java.util.List;

public class CoworkingAppDemo {

    public static void main(String[] args) {
        System.out.println("--- Starting Coworking Space Database Demo ---");

        System.out.println("\n--- Adding New Members ---");
        int member1Id = DatabaseManager.addMember("Alice Wonderland", "alice@example.com", "111-222-3333");
        int member2Id = DatabaseManager.addMember("Bob The Builder", "bob@example.com", "444-555-6666");
        int member3Id = DatabaseManager.addMember("Charlie Chaplin", "charlie@example.com", null); // Member without phone number


        System.out.println("\n--- Adding New Spaces ---");
        int space1Id = DatabaseManager.addSpace("Meeting Room Alpha", "Meeting Room", 8, 50.00);
        int space2Id = DatabaseManager.addSpace("Hot Desk Zone 1", "Hot Desk", 1, 15.00);
        int space3Id = DatabaseManager.addSpace("Private Office 3B", "Private Office", 1, 100.00);


        System.out.println("\n--- Adding Bookings ---");
        if (member1Id != -1 && space1Id != -1) {
            DatabaseManager.addBooking(member1Id, space1Id,
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusHours(2));
            DatabaseManager.addBooking(member1Id, space2Id,
                    LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                    LocalDateTime.now().plusDays(1).withHour(17).withMinute(0));
        }
        if (member2Id != -1 && space1Id != -1) {
            DatabaseManager.addBooking(member2Id, space1Id,
                    LocalDateTime.now().plusHours(3),
                    LocalDateTime.now().plusHours(4));
        }

        System.out.println("\n--- All Members ---");
        List<DatabaseManager.Member> members = DatabaseManager.getAllMembers();
        members.forEach(System.out::println);

        System.out.println("\n--- All Spaces ---");
        List<DatabaseManager.Space> spaces = DatabaseManager.getAllSpaces();
        spaces.forEach(System.out::println);

        System.out.println("\n--- All Bookings ---");
        List<DatabaseManager.Booking> bookings = DatabaseManager.getAllBookings();
        bookings.forEach(System.out::println);

        System.out.println("\n--- Updating Member Phone Number ---");
        int updatedRows = DatabaseManager.updateMemberPhoneNumber("alice@example.com", "999-888-7777");
        System.out.println("Updated " + updatedRows + " row(s) for Alice.");

        System.out.println("\n--- Members After Update ---");
        DatabaseManager.getAllMembers().forEach(System.out::println);

        System.out.println("\n--- Deleting a Booking ---");
        if (!bookings.isEmpty()) {
            int bookingToDeleteId = bookings.get(0).id;
            int deletedRows = DatabaseManager.deleteBooking(bookingToDeleteId);
            System.out.println("Deleted " + deletedRows + " row(s) for booking ID " + bookingToDeleteId);
        } else {
            System.out.println("No bookings to delete.");
        }

        System.out.println("\n--- Bookings After Deletion ---");
        DatabaseManager.getAllBookings().forEach(System.out::println);


        System.out.println("\n--- Coworking Space Database Demo Finished ---");
    }
}
