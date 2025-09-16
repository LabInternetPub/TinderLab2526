package cat.tecnocampus.tinderlab2526.domain;

import cat.tecnocampus.tinderlab2526.domain.exceptions.IsNotCompatibleException;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String email;
	private String nickname;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Gender attraction;

	@Enumerated(EnumType.STRING)
	private Passion passion;

	@OneToMany(
			mappedBy = "origin",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	private List<Like> likes = new ArrayList<>();

	public Profile() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}

	public Gender getGender() {
		return gender;
	}

	public Gender getAttraction() {
		return attraction;
	}

	public Passion getPassion() {
		return passion;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setAttraction(Gender attraction) {
		this.attraction = attraction;
	}

	public void setPassion(Passion passion) {
		this.passion = passion;
	}

	public boolean isCompatible(Profile user) {
		if (user.getId().equals(this.id)) //to avoid narcicists
			return false;
		return (user.getGender() == this.getAttraction() || this.attraction == Gender.Bisexual) && user.getPassion() == this.passion;
	}

	public boolean doesLike(Profile target) {
		return likes.stream().anyMatch(l -> l.getTarget().getId().equals(target.getId()));
	}

	public boolean isMatched(Profile target) {
		Optional<Like> like = likes.stream().filter(l -> l.getTarget().equals(target)).findFirst();
		return like.isPresent() && like.get().isMatched();
	}

	private void setMatch(Profile target) {
		Optional<Like> like = likes.stream().filter(l -> l.getTarget().equals(target)).findFirst();
		if (like.isPresent())
			like.get().setMatched(true);
	}

	//Target must be compatible
	// 1.- Create like
	// 2.- Set like to match if it does
	public Like createAndMatchLike(Profile target) {
		if (!this.isCompatible(target))
			throw new IsNotCompatibleException(this, target);

		Like like = new Like(this, target);
		if (target.doesLike(this)) {
			like.setMatched(true);  	//origin's like set to match
			target.setMatch(this);		//target's like set to match
		}
		this.likes.add(like);
		return like;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Profile profile = (Profile) o;
		return Objects.equals(id, profile.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
