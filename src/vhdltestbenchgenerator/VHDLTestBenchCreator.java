/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vhdltestbenchgenerator;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Daniele
 */
public class VHDLTestBenchCreator {
    
    private static final int MAX_POINTS = 8;
    private static final int MAX_RAM = 19;
    private static final int MAX_VALUE = 256;
    
    private Point fixedPoint;
    private ArrayList<Point> points;
    private String bitmask;
    private String result;

    public VHDLTestBenchCreator() {
        points = new ArrayList<>(MAX_POINTS);
    }
    
    public String generateTestBench() {
        points.clear();
        bitmask = randomizeBitmask();
        randomizePoints();
        result = computeResult();
        StringBuilder output = new StringBuilder();
        output.append(  "library ieee;\n" +
                        "library vunit_lib;\n" +
                        "context vunit_lib.vunit_context;\n" +
                        "use ieee.std_logic_1164.all;\n" +
                        "use ieee.numeric_std.all;\n" +
                        "use ieee.std_logic_unsigned.all;\n" +
                        "use IEEE.std_logic_textio.all;\n" +
                        "use STD.textio.all;\n" +
                        " \n" +
                        "entity project_tb is\n" +
                        "generic (runner_cfg : string);\n" +
                        "end project_tb;\n" +
                        "\n" +
                        "\n" +
                        "architecture projecttb of project_tb is\n" +
                        "constant c_CLOCK_PERIOD		: time := 100 ns;\n" +
                        "signal   tb_done		: std_logic;\n" +
                        "signal   mem_address		: std_logic_vector (15 downto 0) := (others => '0');\n" +
                        "signal   tb_rst		    : std_logic := '0';\n" +
                        "signal   tb_start		: std_logic := '0';\n" +
                        "signal   tb_clk		    : std_logic := '0';\n" +
                        "signal   mem_o_data,mem_i_data		: std_logic_vector (7 downto 0);\n" +
                        "signal   enable_wire  		: std_logic;\n" +
                        "signal   mem_we		: std_logic;\n" +
                        "\n" +
                        "type ram_type is array (65535 downto 0) of std_logic_vector(7 downto 0);\n");
        output.append("signal RAM: ram_type:= (0 => \"" + bitmask + "\",");
        for(int i = 0, j = 1; i < MAX_POINTS; i++) {
            output.append(j + " => \"" + convertToBinary(points.get(i).getX()) + "\", ");
            j++;
            output.append(j + "=> \"" + convertToBinary(points.get(i).getY()) + "\", ");
            j++;
        }
        output.append("17 => \"" + convertToBinary(fixedPoint.getX()) + "\", 18 => \"" + convertToBinary(fixedPoint.getY()) + "\", others => (others => '0'));\n");
        output.append(  "component project_reti_logiche is \n" +
                        "    port (\n" +
                        "            i_clk         : in  std_logic;\n" +
                        "            i_start       : in  std_logic;\n" +
                        "            i_rst         : in  std_logic;\n" +
                        "            i_data       : in  std_logic_vector(7 downto 0); --1 byte\n" +
                        "            o_address     : out std_logic_vector(15 downto 0); --16 bit addr: max size is 255*255 + 3 more for max x and y and thresh.\n" +
                        "            o_done            : out std_logic;\n" +
                        "            o_en         : out std_logic;\n" +
                        "            o_we       : out std_logic;\n" +
                        "            o_data            : out std_logic_vector (7 downto 0)\n" +
                        "          );\n" +
                        "end component project_reti_logiche;\n" +
                        "\n" +
                        "begin \n" +
                        "	UUT: project_reti_logiche\n" +
                        "	port map (\n" +
                        "		  i_clk      	=> tb_clk,	\n" +
                        "          i_start       => tb_start,\n" +
                        "          i_rst      	=> tb_rst,\n" +
                        "          i_data    	=> mem_o_data,\n" +
                        "          o_address  	=> mem_address, \n" +
                        "          o_done      	=> tb_done,\n" +
                        "          o_en   	=> enable_wire,\n" +
                        "		  o_we 	=> mem_we,\n" +
                        "          o_data    => mem_i_data\n" +
                        ");\n" +
                        "\n" +
                        "p_CLK_GEN : process is\n" +
                        "  begin\n" +
                        "    wait for c_CLOCK_PERIOD/2;\n" +
                        "    tb_clk <= not tb_clk;\n" +
                        "  end process p_CLK_GEN; \n" +
                        "  \n" +
                        "  \n" +
                        "MEM : process(tb_clk)\n" +
                        "   begin\n" +
                        "    if tb_clk'event and tb_clk = '1' then\n" +
                        "     if enable_wire = '1' then\n" +
                        "      if mem_we = '1' then\n" +
                        "       RAM(conv_integer(mem_address))              <= mem_i_data;\n" +
                        "       mem_o_data                      <= mem_i_data;\n" +
                        "      else\n" +
                        "       mem_o_data <= RAM(conv_integer(mem_address));\n" +
                        "      end if;\n" +
                        "     end if;\n" +
                        "    end if;\n" +
                        "   end process;\n" +
                        "\n" +
                        "test : process is\n" +
                        "\n" +
                        "    begin\ntest_runner_setup(runner, runner_cfg);\n" +
                        "                        wait for 100 ns;\n" +
                        "                        wait for c_CLOCK_PERIOD;\n" +
                        "                        tb_rst <= '1';\n" +
                        "                        wait for c_CLOCK_PERIOD;\n" +
                        "                        tb_rst <= '0';\n" +
                        "                        wait for c_CLOCK_PERIOD;\n" +
                        "                        tb_start <= '1';\n" +
                        "                        wait for c_CLOCK_PERIOD; \n" +
                        "                        tb_start <= '0';\n" +
                        "                        wait until tb_done = '1';\n" +
                        "                        wait until tb_done = '0';\n" +
                        "                        wait until rising_edge(tb_clk);\n");
        output.append(  "if(RAM(19) /= \"" + result + "\") then\n" +
"                        	assert false severity failure;\n" +
"                       	end if;\n" +
                        "test_runner_cleanup(runner);\n" +
                        "    end process test;\n" +
                        "end projecttb;");
        return output.toString();
    }
    
    private String convertToBinary(int value) {
        StringBuilder binary = new StringBuilder();
        while(value > 0) {
            if(value % 2 == 0)
                binary.append('0');
            else
                binary.append('1');
            value = value / 2;
        }
        while(binary.length() < 8)
            binary.append('0');
        return binary.reverse().toString();
    }
    
    private String randomizeBitmask() {
        Random rand = new Random();
        return convertToBinary(rand.nextInt(MAX_VALUE));
    }
    
    private void randomizePoints() {
        Random rand = new Random();
        for(int i = 0; i < MAX_POINTS; i++)
            points.add(new Point(rand.nextInt(MAX_VALUE), rand.nextInt(MAX_VALUE)));
        fixedPoint = new Point(rand.nextInt(MAX_VALUE), rand.nextInt(MAX_VALUE));
    }
    
    private String computeResult() {
        int distance;
        int min = MAX_VALUE * 2;
        StringBuilder output = new StringBuilder("00000000");
        for(int i = 0; i < MAX_POINTS; i++)
            if(bitmask.charAt(MAX_POINTS - i - 1) == '1') {
                distance = Math.abs(points.get(i).getX() - fixedPoint.getX()) + Math.abs(points.get(i).getY() - fixedPoint.getY());
                if(distance < min) {
                    min = distance;
                    output = new StringBuilder("00000000");
                    output.setCharAt(MAX_POINTS - i - 1, '1');
                }
                else if(distance == min)
                    output.setCharAt(MAX_POINTS - i - 1, '1');
            }
        return output.toString();
    }
    
    
}
