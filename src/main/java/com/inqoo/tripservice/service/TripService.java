package com.inqoo.tripservice.service;

import com.inqoo.tripservice.model.Trip;
import com.inqoo.tripservice.model.exception.NoTripFoundException;
import com.inqoo.tripservice.model.exception.WrongParameters;
import com.inqoo.tripservice.repository.TripJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripService {


        private final TripJpaRepository tripJpaRepository;


        public void saveTrip(Trip trip) {
                tripJpaRepository.save(trip);
        } // logikę biznesową
        public void removeTripById(Integer id) {
                tripJpaRepository.deleteById(id);
        }
        public void updateTrip(Integer id, Trip tripToUpdate) {
                if (id != tripToUpdate.getId()){
                        throw new RuntimeException("Bad request");
                }
                Optional<Trip> maybeTrip = tripJpaRepository.findById(id);
                if (maybeTrip.isEmpty()) { // pobieramy wg id żeby upewnić się, że to będzie aktualizacja
                        // wyrzucenie wyjątku !
                        return;
                }
                tripJpaRepository.save(tripToUpdate);
        }
        public List<Trip> getAllTrips() {
                return tripJpaRepository.findAll();
        }

        public List<Trip> getAllTrips(String nameFragment) {
                List<Trip> result = tripJpaRepository.findAll(); // tu Mockito zwróci to co kazaliśmy
                if (nameFragment != null) {
                        result = result.stream()
                                .filter(c -> c.getDestination().contains(nameFragment))
                                .collect(Collectors.toList());
                }
                return result;
        }
        public List<Trip> getByPrice(double priceFrom, double priceTo) throws NoTripFoundException, WrongParameters {
                if (priceFrom>priceTo){
                        throw new WrongParameters("Wrong input data  "+priceFrom+" and "+priceTo);
                }
                List<Trip> tripsByPrice = tripJpaRepository.findAllByPriceEurBetween(priceFrom, priceTo);

                if (tripsByPrice.isEmpty()) {
                        throw new NoTripFoundException("No Trip with price between "+priceFrom+" and "+priceTo);
                }
                return tripsByPrice;
        }

        public Revisions<Integer, Trip> getAllTripsRevisions(Integer tripId) {
//                return tripJpaRepository.findRevisions(tripId);
                return null;
        }
}