package com.att.publicsafetyhack;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.util.SparseArray;

/**
 * 
 * @author za476x JSON Database getSeats() getUsers() cleanUpDatabase()
 *         validateUsers() bookSeat()
 */

public class JSONDatabase {
	private Context context;
	private Constants k = new Constants();

	// private String usersFileName = "users_file_name";
	private String linkersFileName = "linkersFileName";
	// private String seatsFileName = "seatss_file_name";

	//private ArrayList<Seat> seats = new ArrayList<Seat>();
	private ArrayList<Member> members = new ArrayList<Member>();
	private ArrayList<User> users = new ArrayList<User>();

	/**
	 * @param instance
	 *            of the activity, aka "this"
	 */
	public JSONDatabase(Context instance) {
		context = instance; // getting the activity instance

		initializeResources(); // read the database for resources

	}

	private void initializeResources() {
		populateMembers(); // populates the members arrayList
		populateUsers(); // populates the users arrayList

		// saveLinkers(); //testing
	}

	/**
	 * saves the JSON content to a file
	 * 
	 * @param fileName
	 * @param content
	 *            : string to be saved in the file
	 * @return true of success, otherwise false
	 */
	private boolean saveJSONToFile(String fileName, String content) {
		try {
			FileOutputStream fileOut = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fileOut);
			oos.writeObject(content);
			oos.close();
			fileOut.close();
		} catch (Exception e) {
			Log.d("DEBUG", "error: " + e.toString());
			return false;
		}
		return true; // no errors
	}

	/**
	 * Load a file from internal storage
	 * 
	 * @param fileName
	 * @return String , null if no file found
	 */
	private String loadJSONFile(String fileName) {
		String returnValue = null;
		try {
			FileInputStream fileIn = context.openFileInput(fileName);
			ObjectInputStream ois = new ObjectInputStream(fileIn);
			returnValue = (String) ois.readObject();
			ois.close();
			fileIn.close();
		} catch (Exception e) {
			// if file had nothing inside
			Log.d("DEBUG",
					"Error in loading file " + fileName + ". " + e.toString());

			return null;
		}
		return returnValue;
	}

	/**
	 * reads the JSON file and returns the file as a string, this is only used
	 * to initialize the database with default values only read if dynamic
	 * resource files returns null
	 * 
	 * @param resource
	 *            id
	 * @return String
	 */
	private String readJSONFile(int id) {
		InputStream is = context.getResources().openRawResource(id);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		try {
			Reader reader = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			is.close();
		} catch (Exception e) {
			//Log.d(k.TAG, "Error in loading default values");
		}
		return writer.toString();
	}

	private void populateMembers() {

		/*String seatsFile = readJSONFile(R.raw.seats);
		try {
			JSONObject jsonOb = new JSONObject(seatsFile); // open the object
			JSONArray jsonArr = jsonOb.getJSONArray("seats"); // open the seat
																// array
			for (int i = 0; i < jsonArr.length(); i++) {
				// populate the seats arraylist with JSON values
				Seat seat = new Seat();
				seat.setName(jsonArr.getJSONObject(i).getString("name") + "");
				seat.setType(jsonArr.getJSONObject(i).getString("type"));

				if (!jsonArr.getJSONObject(i).getString("phone").equals("no")) {
					seat.setPhoneNumber(jsonArr.getJSONObject(i).getString(
							"phone"));
				}

				if (!jsonArr.getJSONObject(i).getString("printer").equals("no")) {
					seat.setPrinterNumber(jsonArr.getJSONObject(i).getString(
							"printer"));
				}

				if (jsonArr.getJSONObject(i).getString("available")
						.equals("yes")) {
					seat.setAvailable(true);
				} else {
					seat.setAvailable(false);
				}

				// add the seat to the arrayList
				seats.add(seat);

				// this is test code
				Log.d(k.TAG, jsonArr.getJSONObject(i).getString("name") + "\n");
			}

		} catch (JSONException e) {
			Log.d(k.TAG, "Error in parsing seats json");
		}*/
	}

	private void populateUsers() {
		/*String usersFile = readJSONFile(R.raw.users);
		try {
			JSONObject jsonOb = new JSONObject(usersFile); // open the object
			JSONArray jsonArr = jsonOb.getJSONArray("users"); // open the seat
																// array
			for (int i = 0; i < jsonArr.length(); i++) {
				// populate the users arrayList with JSON values
				User user = new User();
				user.setName(jsonArr.getJSONObject(i).getString("name"));
				user.setUsername(jsonArr.getJSONObject(i).getString("username"));
				user.setPassword(jsonArr.getJSONObject(i).getString("password"));
				// add the user to the arrayList
				users.add(user);
			}
		} catch (JSONException e) {
			Log.d(k.TAG, "Error in parsing users json " + e);
		}*/
	}

	/*
	 * get all the seats
	 */
	public ArrayList<Member> getSeats() {
		return members;
	}

	// get one seat based on name
	public Member getSeat(String name) {
		for (Member member : members) {
			if (member.getName().equals(name)) {
				return member;
			}
		}
		return null;
	}

	/**
	 * get Users
	 */
	public ArrayList<User> getUsers() {
		return users;
	}

	/**
	 * \ Get user info based on their username
	 * 
	 * @param username
	 * @return
	 */
	public User getUserWithUsername(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	/**
	 * Validates the username and password against the database
	 * 
	 * @param username
	 * @param password
	 * @return true/false
	 */
	public boolean validateUser(String username, String password) {
		for (User user : users) {
			if (user.getUsername().equals(username)
					&& user.getPassword().equals(password)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * User container class
	 * 
	 * @author ZA476x
	 * 
	 */
	class User {
		private String username, password, name;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * Linker container class, help links the users to seats with a specified
	 * date
	 * 
	 * @author ZA476x
	 * 
	 */
	public class Linker {
		private String seat, user;
		private Date date;

		public String getSeat() {
			return seat;
		}

		public void setSeat(String seat) {
			this.seat = seat;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}
}