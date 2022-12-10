package com.inqoo.tripservice.controler;


import com.inqoo.tripservice.model.Trip;
import com.inqoo.tripservice.model.exception.ErrorMessage;
import com.inqoo.tripservice.model.exception.NoTripFoundException;
import com.inqoo.tripservice.model.exception.WrongParameters;
import com.inqoo.tripservice.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.history.Revisions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("trips")
public class TripController {

    @Autowired
    private TripService tripService;

    //path
    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity createTrip(@RequestBody Trip trip) {
        tripService.saveTrip(trip);

        URI savedCityUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(trip.getId())
                .toUri();
        return ResponseEntity.created(savedCityUri).build();
    }

    @GetMapping(path = "/trips", produces = "application/json")
    public List<Trip> trips(@RequestParam(name="tripDestinationFragment", required = false) String nameFragment){
        System.out.println("Zapytanie zawierało parametr 'tripDestinationFragment' o wartości: "+nameFragment);
        return tripService.getAllTrips(nameFragment);
    }
    @GetMapping(path = "/ByPrice", produces = "application/json")
    public List<Trip> tripsByPrice(@RequestParam double priceFrom, @RequestParam double priceTo) {
        try {
            return tripService.getByPrice(priceFrom, priceTo);
        } catch (NoTripFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping(path = "/{tripId}", produces = "application/json")
    public Trip tripsById(@PathVariable("tripId") Integer id){
        return tripService.getAllTrips(null).get(id);
    }
    @ExceptionHandler(NoTripFoundException.class) // jaki wyjątek obsługujemy
    @ResponseStatus(HttpStatus.NOT_FOUND) // jaki kod HTTP zwrócimy
    public ResponseEntity<ErrorMessage> handleNoTripFoundException(NoTripFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body( new ErrorMessage(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @GetMapping(path = "/{tripId}/revisions",produces = "application/json")
    public List<Trip> tripRevisionById(@PathVariable("tripId") Integer id){
        Revisions<Integer,Trip> allTripRevisions = tripService.getAllTripsRevisions(id);
        List<Trip> tripChanges = allTripRevisions.get()
                .map(r->r.getEntity())
                .collect(Collectors.toList());
        return tripChanges;
    }

    @ExceptionHandler(WrongParameters.class) // jaki wyjątek obsługujemy
    @ResponseStatus(HttpStatus.BAD_REQUEST) // jaki kod HTTP zwrócimy
    public ResponseEntity<ErrorMessage> handleBadParamerersException(WrongParameters exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body( new ErrorMessage(exception.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }
    @DeleteMapping(path = "/{id}")
    public void removeTrip(@PathVariable Integer id) {
        tripService.removeTripById(id);
    }
    @PutMapping(path = "/{id}")
    public ResponseEntity updateTrip(@PathVariable Integer id, @RequestBody Trip trip) {
        tripService.updateTrip(id, trip);
        return ResponseEntity.noContent().build();
    }
}