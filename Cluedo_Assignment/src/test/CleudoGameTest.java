//package test;
//
//import CluedoMain.CleudoGame;
//
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//
//import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.*;
//
//class CleudoGameTest {
//    /**IN ORDER TO USE THIS TEST THE isTesting field of board class and CleudoGame class should be set to true*/
//    /**IN ORDER TO USE THIS TEST THE isTesting field of board class and CleudoGame class should be set to true*/
//    /**IN ORDER TO USE THIS TEST THE isTesting field of board class and CleudoGame class should be set to true*/
//    /**IN ORDER TO USE THIS TEST THE isTesting field of board class and CleudoGame class should be set to true*/
//    /**IN ORDER TO USE THIS TEST THE isTesting field of board class and CleudoGame class should be set to true*/
//    @org.junit.jupiter.api.Test
//    void testGameSetUp() {
//
//        InputStream stdin = System.in;
//        try {
//            File inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/CleudoGameSetups");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            CleudoGame g = new CleudoGame();
//            g.isTesting = true;
//            assertTrue(g.getPlayers().length == 6);
//            assertTrue(g.getPlayers()[0].getCharacter().getName().equals("Miss Scarlett"));
//            assertTrue(g.getPlayers()[0].getName().equals("rain man"));
//            assertTrue(g.getPlayers()[5].getName().equals("5"));
//            assertTrue(g.getDeck().isEmpty());
//            assertTrue(g.getSuggestion() == null);
//            assertTrue(g.getAccusation() == null);
//            assertTrue(g.getCandidates().isEmpty());
//            assertTrue(g.getMurder() != null);
//            assertTrue(g.getCharacters() != null);
//            assertTrue(g.getBoard() != null);
//
//        }
//        catch (IOException e){
//            System.out.println(e.getMessage());
//        }finally {
//            System.setIn(stdin);
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    void testBoard() {
//        InputStream stdin = System.in;
//        try {
//            File inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/CleudoGameSetups");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            CleudoGame g = new CleudoGame();
//            assertFalse(g.getBoard().getRooms().isEmpty());
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }finally {
//            System.setIn(stdin);
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    void testMovement() {
//        InputStream stdin = System.in;
//        try {
//            File inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/CleudoGameSetups");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            CleudoGame g = new CleudoGame();
//            g.isTesting = true;
//            inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/testGameMovement");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            g.play();
//            assertEquals(g.getBoard().getSquares()[19][6], g.getPlayers()[0].getSquare());
//
//        }
//        catch (IOException | NullPointerException e){
//            System.out.println(e.getMessage());
//        }finally {
//            System.setIn(stdin);
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    void testSuggestion() {
//        InputStream stdin = System.in;
//        try {
//            File inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/CleudoGameSetups");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            CleudoGame g = new CleudoGame();
//            g.isTesting = true;
//            g.getBoard().istesting = true;
//            inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/testSuggestion");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            g.play();
//            assertEquals("Lounge", g.getCandidates().get(0).getRoom().getName());
//            assertTrue(g.getPlayers()[0].canWin() == false);
//
//        }
//        catch (IOException | NullPointerException e){
//            System.out.println(e.getMessage());
//        }finally {
//            System.setIn(stdin);
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    void testAccusationRefuted() {
//        InputStream stdin = System.in;
//        try {
//            File inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/CleudoGameSetups");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            CleudoGame g = new CleudoGame();
//            g.isTesting = true;
//            inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/testAccusationRefuted");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            g.play();
//            assertTrue(g.getPlayers()[0].canWin() == false);
//
//        }
//        catch (IOException | NullPointerException e){
//            System.out.println(e.getMessage());
//        }finally {
//            System.setIn(stdin);
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    void testAccusationNotRefuted() {
//        InputStream stdin = System.in;
//        try {
//            File inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/CleudoGameSetups");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            CleudoGame g = new CleudoGame();
//            g.isTesting = true;
//            inputs = new File("/home/zhuxiny/IdeaProjects/swen 225 assignment 1/src/test/testAccusationNotRefuted");
//            System.setIn(new ByteArrayInputStream(Files.readAllBytes(inputs.toPath())));
//            g.play();
//            assertTrue(g.getPlayers()[0].canWin());
//
//
//        }
//        catch (IOException | NullPointerException e){
//            System.out.println(e.getMessage());
//        }finally {
//            System.setIn(stdin);
//        }
//    }
//}