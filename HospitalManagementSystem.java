package HospitalManagmentSystem;

import org.w3c.dom.ls.LSOutput;

import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url ="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="root";

    public static void main(String[] args) {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        Scanner scanner=new Scanner(System.in);
        try{
            Connection connection= DriverManager.getConnection(url, username, password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patient");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appoinment");
                System.out.println("5. Exit");
                System.out.println("Enter your choice");
                int choice =scanner.nextInt();

                switch (choice)
                {
                    case 1:
                        //ADD PATIENT
                        patient.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        //VIEW PATIENT
                        patient.veiwPatient();
                        System.out.println();
                        break;
                    case 3:
                        //VIEW DOCTOR
                        doctor.veiwDoctotr();
                        System.out.println();
                        break;
                    case 4:
                        //BOOK APPOINMENTS
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;

                    case 5:
                        return;
                    default:
                        System.out.println("enter valid choice");
                        break;
                }
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static void bookAppointment(Patient patient,Doctor doctor,Connection connection,Scanner scanner)
    {
        System.out.println("Enter Patient Id: ");
        int PatientId =scanner.nextInt();
        System.out.println("Enter Doctor Id: ");
        int DoctorId = scanner.nextInt();
        System.out.println("Enter Appointment Date(YYYY-MM-DD: ");
        String appointmentDate =scanner.next();
        if(patient.getPatientById(PatientId) && doctor.getDoctorById(DoctorId)){
            if(cheakDoctorAvailabilty(DoctorId,appointmentDate,connection))
            {
                String appointmentQuery =" insert into appoinments(patient_id,doctor_id,appointment_date) values (?,?,?)";
                try{
                    PreparedStatement preparedStatement=connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1,PatientId);
                    preparedStatement.setInt(2,DoctorId);
                    preparedStatement.setString(3,appointmentDate);
                    int rowsAffected =preparedStatement.executeUpdate();
                    if(rowsAffected >0)
                    {
                        System.out.println("Appointment book");
                    }else {
                        System.out.println("Failed to Book Appointment" );
                    }

                }catch(SQLException e)
                {
                    e.printStackTrace();
                }
            }else {
                System.out.println("Doctor not available on this date");
            }
        }else{
            System.out.println("Either Doctor or Patient Doesn't Exits!!");
        }

    }
    public static boolean cheakDoctorAvailabilty(int DoctorId,String appointmentDate,Connection connection)
    {
        String query="select count(*) appoinments where doctor_id = ? and appointment_date= ?";
        try
        {
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,DoctorId);
            preparedStatement.setString(2,appointmentDate);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next())
            {
                int count=resultSet.getInt(1);
                if(count==0)
                {
                    return true;
                }else {
                    return false;
                }
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;

    }

}
