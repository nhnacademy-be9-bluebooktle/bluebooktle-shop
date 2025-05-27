// package shop.bluebooktle.backend.point.entity;
//
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.OneToOne;
// import jakarta.persistence.Table;
// import lombok.AccessLevel;
// import lombok.EqualsAndHashCode;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.ToString;
//
// @Entity
// @Table(name = "review_point_history")
// @Getter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
// @EqualsAndHashCode(of = "id", callSuper = false)
// @ToString(exclude = {"review"})
// public class ReviewPointHistory {
// 	@Id
// 	@GeneratedValue(strategy = GenerationType.IDENTITY)
// 	@Column(name = "review_point_id")
// 	private Long id;
//
// 	@OneToOne(fetch = FetchType.LAZY, optional = false)
// 	@JoinColumn(name = "point_id", nullable = false, unique = true)
// 	private PointHistory pointHistory;
//
// 	@OneToOne(fetch = FetchType.LAZY)
// 	@JoinColumn(name = "review_id", nullable = false, unique = true)
// 	private Review review;
// }
