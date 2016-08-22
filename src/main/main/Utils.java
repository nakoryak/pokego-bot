
import com.annimon.stream.Collectors;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.EggIncubator;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.api.inventory.Item;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by alexander.nakoryakov on 19.08.2016.
 */
public class Utils {
    public static Pokestop findNearestPokeStop(Collection<Pokestop> pokestops) {
        Optional<Pokestop> pokestop = pokestops.stream().filter(x -> x.canLoot()).sorted((o1, o2) -> (int) (o1.getDistance() - o2.getDistance())).findFirst();
        return pokestop.get();
    }

    public static void printPokemons(PokemonGo go) throws LoginFailedException, RemoteServerException {
        long l = go.getInventories().getPokebank().getPokemons().stream().count();
        System.out.println(l);
        go.getInventories().getPokebank().getPokemons().stream().forEach(x -> {
            System.out.print(x.getIvRatio() + " ");
            System.out.println(x.getCp() + " " + x.getPokemonId().name());
        });
    }

    public static void egg(PokemonGo go) throws Exception {
        List<EggIncubator> incubators = go.getInventories().getIncubators();
        for (EggIncubator incubator: incubators){
            if (!incubator.isInUse()){
                incubator.hatchEgg(go.getInventories().getHatchery().getEggs().stream().findFirst().get());
                System.out.println("===NEW=== " + incubator.getKmLeftToWalk());
            }
            System.out.println("Left to walk  " + incubator.getKmLeftToWalk());
        }
    }

    public static void printInventory(PokemonGo go) throws LoginFailedException, RemoteServerException {
        go.getInventories().getItemBag().getItems().stream();
    }

    public static void soldPokemonIfBetterExist(CatchablePokemon catchablePokemon, PokemonGo go) throws LoginFailedException, RemoteServerException {
        go.getInventories().getPokebank().getPokemons().stream()
                .filter(x -> x.getPokemonId().equals(catchablePokemon.getPokemonId()))
                .forEach(pok -> {
                    //if (pok.getCp() > c)
                });

    }

    public static void mainLoop(PokemonGo go) throws LoginFailedException, RemoteServerException, NoSuchItemException, InterruptedException {

        Runnable thread = () -> {
            try {
                Utils.egg(go);
                Thread.sleep(60000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread th = new Thread(thread);
        th.start();

        while (true) {
            for (NearbyPokemon nearby : go.getMap().getNearbyPokemon()) {
                System.out.println(nearby.getDistanceInMeters() + " " + nearby.getPokemonId().name());
            }

            for (Pokestop pokestop : go.getMap().getMapObjects().getPokestops()) {
                if (pokestop.inRange() && pokestop.canLoot()) {
                    PokestopLootResult result = pokestop.loot();
                    result.getItemsAwarded().stream().forEach(x -> System.out.println(x.getItemId().name()));
                }
            }

            List<CatchablePokemon> catchablePokemon = go.getMap().getCatchablePokemon();
            System.out.println("Pokemon in area:" + catchablePokemon.size());

            for (CatchablePokemon cp : go.getMap().getCatchablePokemon()) {
                EncounterResult encResult = cp.encounterPokemon();
                if (encResult.wasSuccessful()) {
                    System.out.println("Encounted:" + cp.getPokemonId());
                    CatchResult result = cp.catchPokemon();
                    System.out.println("Attempt to catch:" + cp.getPokemonId() + " " + result.getStatus());
                }
            }

            Random random = new Random();

            //Double newLatitude = go.getLatitude() + random.nextDouble() * 0.0001;
            //Double newLongitude = go.getLongitude() + random.nextDouble() * 0.0001;

            Double newLatitude;
            Double newLongitude;

            try {
                newLatitude = findNearestPokeStop(go.getMap().getMapObjects().getPokestops()).getLatitude();
                newLongitude = findNearestPokeStop(go.getMap().getMapObjects().getPokestops()).getLongitude();
            } catch (Exception e){
                newLatitude = go.getLatitude() + random.nextDouble() * 0.001;
                newLongitude = go.getLongitude() + random.nextDouble() * 0.001;
            }
            System.out.println(newLatitude);
            System.out.println(newLongitude);

            go.setLatitude(newLatitude);
            go.setLongitude(newLongitude);
            System.out.println(go.getInventories().getIncubators().get(0).getHatchDistance());
            sleep(2000);
        }
    }
}
