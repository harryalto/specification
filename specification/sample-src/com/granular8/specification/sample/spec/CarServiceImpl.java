package com.granular8.specification.sample.spec;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.granular8.specification.sample.domain.Car;
import static com.granular8.specification.sample.domain.Color.RED;
import com.granular8.specification.sample.domain.Region;
import static com.granular8.specification.sample.domain.Region.*;
import com.granular8.specification.spec.Specification;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

/**
 * Default implementation of the {@link com.granular8.specification.sample.spec.CarService} interface.
 */
public class CarServiceImpl implements CarService {

  private CarRepository repository;

  /**
   * {@inheritDoc}
   */
  public void setRepository(final CarRepository repository) {
    this.repository = repository;
  }

  /**
   * {@inheritDoc}
   */
  public Collection<Car> findCandidateCars() {

    final Collection<Car> keepers = new HashSet<Car>();

    final CalendarDate today = Clock.now().calendarDate(TimeZone.getTimeZone("GMT"));

    final Specification approvedAge = new CarAgeSpecification(today, 5);
    final Specification colorRed = new CarColorSpecification(RED);
    final Specification convertible = new ConvertibleCarSpecification();
    final Specification approvedState = new CarOwnerRegionSpecification(getAuthorizedRegions());

    final Specification candidateCarSpecification = colorRed.and(approvedState.and(approvedAge).or(convertible));

    final Collection<Car> cars = repository.findAllCarsInStock();

    for (Car car : cars) {
      if (candidateCarSpecification.isSatisfiedBy(car))
        keepers.add(car);
    }

    return keepers;
  }

  private Set<Region> getAuthorizedRegions() {
    Set<Region> regions = new HashSet<Region>();
    regions.add(SOUTH_WEST);
    regions.add(SOUTH_EAST);
    regions.add(SOUTH);

    return regions;
  }
}
