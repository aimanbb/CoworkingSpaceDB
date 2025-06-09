import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/coworking_db";
    private static final String DB_USER = "coworking_user";
    private static final String DB_PASSWORD = "your_secure_password";

    /**
     * @return A `Connection` object if successful, otherwise null.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * @param connection The `Connection` object to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static class Member {
        public int id;
        public String name;
        public String email;
        public String phoneNumber;
        public LocalDateTime joinDate;

        public Member(int id, String name, String email, String phoneNumber, LocalDateTime joinDate) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.joinDate = joinDate;
        }

        @Override
        public String toString() {
            return "Member{id=" + id + ", name='" + name + "', email='" + email + "', phone='" + phoneNumber + "', joinDate=" + joinDate + '}';
        }
    }

    public static class Space {
        public int id;
        public String name;
        public String type;
        public int capacity;
        public double hourlyRate;

        public Space(int id, String name, String type, int capacity, double hourlyRate) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.capacity = capacity;
            this.hourlyRate = hourlyRate;
        }

        @Override
        public String toString() {
            return "Space{id=" + id + ", name='" + name + "', type='" + type + "', capacity=" + capacity + ", rate=" + hourlyRate + '}';
        }
    }

    public static class Booking {
        public int id;
        public int memberId;
        public int spaceId;
        public LocalDateTime startTime;
        public LocalDateTime endTime;

        public Booking(int id, int memberId, int spaceId, LocalDateTime startTime, LocalDateTime endTime) {
            this.id = id;
            this.memberId = memberId;
            this.spaceId = spaceId;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public String toString() {
            return "Booking{id=" + id + ", memberId=" + memberId + ", spaceId=" + spaceId + ", startTime=" + startTime + ", endTime=" + endTime + '}';
        }
    }

    /**
     * @param memberName The name of the member.
     * @param email The email of the member (must be unique).
     * @param phoneNumber The phone number of the member (optional).
     * @return The ID of the newly inserted member, or -1 if insertion fails.
     */
    public static int addMember(String memberName, String email, String phoneNumber) {
        String SQL = "INSERT INTO members(name, email, phone_number) VALUES(?, ?, ?) RETURNING member_id;";
        int newMemberId = -1;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, memberName);
            pstmt.setString(2, email);
            pstmt.setString(3, phoneNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                newMemberId = rs.getInt(1); // Get the returned ID
                System.out.println("Member added: " + memberName + " with ID " + newMemberId);
            }
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            e.printStackTrace();
        }
        return newMemberId;
    }

    /**
     * @param name The name of the space (e.g., "Meeting Room Alpha").
     * @param type The type of space (e.g., "Meeting Room", "Hot Desk").
     * @param capacity The capacity of the space.
     * @param hourlyRate The hourly rate for the space.
     * @return The ID of the newly inserted space, or -1 if insertion fails.
     */
    public static int addSpace(String name, String type, int capacity, double hourlyRate) {
        String SQL = "INSERT INTO spaces(name, type, capacity, hourly_rate) VALUES(?, ?, ?, ?) RETURNING space_id;";
        int newSpaceId = -1;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setInt(3, capacity);
            pstmt.setDouble(4, hourlyRate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                newSpaceId = rs.getInt(1);
                System.out.println("Space added: " + name + " with ID " + newSpaceId);
            }
        } catch (SQLException e) {
            System.err.println("Error adding space: " + e.getMessage());
            e.printStackTrace();
        }
        return newSpaceId;
    }

    /**
     * @param memberId The ID of the member.
     * @param spaceId The ID of the space.
     * @param startTime The start time of the booking.
     * @param endTime The end time of the booking.
     * @return The ID of the newly inserted booking, or -1 if fails.
     */
    public static int addBooking(int memberId, int spaceId, LocalDateTime startTime, LocalDateTime endTime) {
        String SQL = "INSERT INTO bookings(member_id, space_id, start_time, end_time) VALUES(?, ?, ?, ?) RETURNING booking_id;";
        int newBookingId = -1;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, spaceId);
            pstmt.setTimestamp(3, Timestamp.valueOf(startTime));
            pstmt.setTimestamp(4, Timestamp.valueOf(endTime));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                newBookingId = rs.getInt(1);
                System.out.println("Booking added: ID " + newBookingId + " for member " + memberId + " in space " + spaceId);
            }
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
            e.printStackTrace();
        }
        return newBookingId;
    }

    /**
     * @return A List of Member objects.
     */
    public static List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String SQL = "SELECT member_id, name, email, phone_number, join_date FROM members;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                int id = rs.getInt("member_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phone = rs.getString("phone_number");
                Timestamp joinTimestamp = rs.getTimestamp("join_date");
                LocalDateTime joinDate = (joinTimestamp != null) ? joinTimestamp.toLocalDateTime() : null;
                members.add(new Member(id, name, email, phone, joinDate));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving members: " + e.getMessage());
            e.printStackTrace();
        }
        return members;
    }

    /**
     * @return A List of Space objects.
     */
    public static List<Space> getAllSpaces() {
        List<Space> spaces = new ArrayList<>();
        String SQL = "SELECT space_id, name, type, capacity, hourly_rate FROM spaces;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                int id = rs.getInt("space_id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                int capacity = rs.getInt("capacity");
                double hourlyRate = rs.getDouble("hourly_rate");
                spaces.add(new Space(id, name, type, capacity, hourlyRate));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving spaces: " + e.getMessage());
            e.printStackTrace();
        }
        return spaces;
    }

    /**
     * @return A List of Booking objects.
     */
    public static List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String SQL = "SELECT booking_id, member_id, space_id, start_time, end_time FROM bookings;";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {
            while (rs.next()) {
                int id = rs.getInt("booking_id");
                int memberId = rs.getInt("member_id");
                int spaceId = rs.getInt("space_id");
                LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = rs.getTimestamp("end_time").toLocalDateTime();
                bookings.add(new Booking(id, memberId, spaceId, startTime, endTime));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving bookings: " + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }


    /**
     * @param email The email of the member to update.
     * @param newPhoneNumber The new phone number.
     * @return The number of rows updated.
     */
    public static int updateMemberPhoneNumber(String email, String newPhoneNumber) {
        String SQL = "UPDATE members SET phone_number = ? WHERE email = ?;";
        int rowsUpdated = 0;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, newPhoneNumber);
            pstmt.setString(2, email);
            rowsUpdated = pstmt.executeUpdate();
            System.out.println("Updated phone number for " + email + ". Rows affected: " + rowsUpdated);
        } catch (SQLException e) {
            System.err.println("Error updating member phone: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsUpdated;
    }

    /**
     * Deletes a booking by its ID.
     *
     * @param bookingId The ID of the booking to delete.
     * @return The number of rows deleted.
     */
    public static int deleteBooking(int bookingId) {
        String SQL = "DELETE FROM bookings WHERE booking_id = ?;";
        int rowsDeleted = 0;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, bookingId);
            rowsDeleted = pstmt.executeUpdate();
            System.out.println("Deleted booking with ID: " + bookingId + ". Rows affected: " + rowsDeleted);
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            e.printStackTrace();
        }
        return rowsDeleted;
    }


}
