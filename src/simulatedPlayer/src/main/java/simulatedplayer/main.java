//package simulatedplayer;
//
//import chessserver.Enums.ComputerDifficulty;
//import chessserver.Enums.Gametype;
//
//public class main {
//    public static void main(String[] args) {
//        SimulatedPlayer player = new SimulatedPlayer();
//
//        Runtime.getRuntime().addShutdownHook(new Thread(player::shutdown));
//
//        while(true) {
//            if(!player.isFull()) {
//                Gametype randomType = Gametype.random();
//                ComputerDifficulty randomDifficulty = ComputerDifficulty.random();
//                player.joinNewGame(randomType, randomDifficulty);
//
//                System.out.println("New game: " + randomType.getStrVersion() + ", difficulty elo: " + randomDifficulty.eloRange);
//            }
//
//            try{
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }
//    }
//}
