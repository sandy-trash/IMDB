package testcases;

public class MovieDetails {

	private String movie;
	private String rating;
	private String year;

	public MovieDetails(String movie, String rating, String year) {
		this.movie = movie;
		this.rating = rating;
		this.year = year;
	}

	public String getMovie() {
		return this.movie;
	}

	public String getRating() {
		return this.rating;
	}

	public String getYear() {
		return this.year;
	}
}
