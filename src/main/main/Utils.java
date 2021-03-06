
import POGOProtos.Enums.PokemonFamilyIdOuterClass;
import POGOProtos.Networking.Responses.UseItemEggIncubatorResponseOuterClass;
import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.inventory.EggIncubator;
import com.pokegoapi.api.inventory.Inventories;
import com.pokegoapi.api.inventory.Item;
import com.pokegoapi.api.inventory.Pokeball;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.EvolutionResult;
import com.pokegoapi.api.map.pokemon.NearbyPokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.exceptions.EncounterFailedException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;

import java.util.*;
import java.util.stream.*;
import static java.lang.Thread.sleep;

/**
 * Created by alexander.nakoryakov on 19.08.2016.
 */
public class Utils {
    public static Pokestop findNearestPokeStop(Collection<Pokestop> pokestops) {
        Optional<Pokestop> pokestop = pokestops.stream().filter(x -> x.canLoot()).sorted((o1, o2) -> (int) (o2.getDistance() - o1.getDistance())).findFirst();
        return pokestop.get();
    }

    private static long countPokestops(Collection<Pokestop> pokestops){
        return pokestops.stream().count();
    }

    public static void printAllCandies(PokemonGo go) throws LoginFailedException, RemoteServerException {
        PokemonFamilyIdOuterClass.PokemonFamilyId[] familyId = PokemonFamilyIdOuterClass.PokemonFamilyId.values();
        for (PokemonFamilyIdOuterClass.PokemonFamilyId id: familyId) {
            System.out.println(id.name() + " " + go.getInventories().getCandyjar().getCandies(id));
        }
    }

    public static void printPokemons(PokemonGo go) throws LoginFailedException, RemoteServerException {
        long l = go.getInventories().getPokebank().getPokemons().stream().count();
        System.out.println(l);
        go.getInventories().getPokebank().getPokemons().stream().sorted((o1, o2) -> o2.getCp() - o1.getCp()).forEach(x -> {
            System.out.print(x.getIvRatio() + " ");
            System.out.println(x.getCp() + " " + x.getPokemonId().name());
        });
    }

    public static void egg(PokemonGo go) throws Exception {
        List<EggIncubator> incubators = go.getInventories().getIncubators();
        for (EggIncubator incubator: incubators){
            if (!incubator.isInUse()){
                go.getInventories().getHatchery().queryHatchedEggs().stream().forEach(hatchedEgg -> {
                    System.out.println("Id: " + hatchedEgg.getId());
                    System.out.println("Candy: " + hatchedEgg.getCandy());
                    System.out.println("Stardust: " + hatchedEgg.getStardust());
                    System.out.println("Experience: " + hatchedEgg.getExperience());
                });
                System.out.println("Count eggs " + go.getInventories().getHatchery().getEggs().stream().count());
                System.out.println("asds" + go.getInventories().getHatchery().getEggs().stream().filter(x-> x.isIncubate()).count());
                UseItemEggIncubatorResponseOuterClass.UseItemEggIncubatorResponse.Result result =
                        incubator.hatchEgg(go.getInventories().getHatchery().getEggs().stream().filter(x -> !x.isIncubate()).findFirst().get());
                System.out.println("-------------" +result.name());
                System.out.println("===NEW=== " + incubator.getKmLeftToWalk());
            }

            System.out.println("Left to walk  " + incubator.getKmLeftToWalk());
        }
    }

    public static void printInventory(PokemonGo go) throws LoginFailedException, RemoteServerException {
        Collection<Item> map = go.getInventories().getItemBag().getItems();
        for (Item item: map){
            System.out.println(item.getItemId().name() + " " + item.getCount());
        }
    }

    public static void soldPokemonIfBetterExist(CatchablePokemon cp, PokemonGo go) throws LoginFailedException, RemoteServerException {

        go.getInventories().getPokebank().getPokemons().stream()
                .filter(x -> x.getPokemonId().equals(cp.getPokemonId()))
        //        .filter()
                .forEach(pok -> {
                    //if (pok.getCp() > c)
                });

    }

    public static void mainLoop(PokemonGo go) throws LoginFailedException, RemoteServerException, NoSuchItemException, InterruptedException, EncounterFailedException {

        int count = 0;
        Runnable thread = () -> {
            while (true) {
                try {
                    Utils.egg(go);
                    Thread.sleep(60000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                    result.getItemsAwarded().forEach(x -> System.out.println(x.getItemId().name()));
                }
            }

            List<CatchablePokemon> catchablePokemon = go.getMap().getCatchablePokemon();
            System.out.println("Pokemon in area:" + catchablePokemon.size());

            for (CatchablePokemon cp : go.getMap().getCatchablePokemon()) {
                EncounterResult encResult = cp.encounterPokemon();
                if (encResult.wasSuccessful()) {
                    System.out.println("Encounted:" + cp.getPokemonId());
                    CatchResult result = cp.catchPokemonBestBallToUse();
                    System.out.println("Attempt to catch:" + cp.getPokemonId() + " " + result.getStatus() + " " + encResult.getPokemonData().getCp());
                    /*if (!result.isFailed()) {
                        go.getInventories().getPokebank().getPokemons().stream()
                                .filter(x -> x.getCp() < 1000)
                                .filter(x -> x.getIvRatio() < 0.85)
                                .filter(x -> x.getPokemonId().equals(cp.getPokemonId()))
                                .forEach(x -> {
                                    if (x.getCp() < encResult.getPokemonData().getCp()) {
                                        try {
                                            System.out.println("Transferred: " + x.getPokemonId().name() + " " + x.getCp());
                                            x.transferPokemon();
                                        } catch (Exception e) {

                                        }
                                    }
                                });
                    }*/
                    System.out.println("Catched in this session: " + ++count);
                }
            }

                Random random = new Random();

                //Double newLatitude = go.getLatitude() + random.nextDouble() * 0.0001;
                //Double newLongitude = go.getLongitude() + random.nextDouble() * 0.0001;

                Double newLatitude;
                Double newLongitude;

                Collection<Pokestop> pokestops = go.getMap().getMapObjects(30).getPokestops();
                System.out.println("Nearby pokestops: " + countPokestops(pokestops) + " can loot: " + pokestops.stream().filter(x -> x.canLoot()).count());
                try {
                    System.out.println(findNearestPokeStop(pokestops).getDetails().getDescription());
                    newLatitude = findNearestPokeStop(pokestops).getLatitude();
                    newLongitude = findNearestPokeStop(pokestops).getLongitude();
                } catch (Exception e) {
                    newLatitude = go.getLatitude() + random.nextGaussian() * 0.0005;
                    newLongitude = go.getLongitude() + random.nextGaussian() * 0.0005;
                }
                System.out.println(newLatitude);
                System.out.println(newLongitude);

                go.setLatitude(newLatitude);
                go.setLongitude(newLongitude);
                sleep(2000);
        }
    }

    public static void removePokemons(PokemonGo go) throws LoginFailedException, RemoteServerException {
        go.getInventories().getPokebank().getPokemons().stream().filter(x -> x.getIvRatio() < 0.85)
                .filter(x -> x.getCp() < 800).forEach(x -> {
            try {
                System.out.println("Transferring: " + x.getPokemonId().name() + " " + x.getCp());
                x.transferPokemon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void evolveAll(PokemonGo go) throws LoginFailedException, RemoteServerException {
        go.getInventories().getPokebank().getPokemons().stream()
                .filter(x -> x.getEvolutionForm().getEvolutionStage() == 1)
                .forEach(x -> {
                    System.out.println("Trying  to evolve");
                    try {
                        EvolutionResult result = x.evolve();
                        if (result.isSuccessful()) {
                            System.out.println("Evolved: " +result.getEvolvedPokemon().getPokemonId().name() + " " + result.getEvolvedPokemon().getCp());
                        }
                    } catch (Exception e) {
                        System.out.println(x.getPokemonId().name() + " cannot be evolved");
                    }
                });
    }
}
