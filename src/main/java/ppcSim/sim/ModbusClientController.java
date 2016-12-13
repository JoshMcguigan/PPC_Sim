package ppcSim.sim;

import com.digitalpetri.modbus.ExceptionCode;
import com.digitalpetri.modbus.requests.ReadHoldingRegistersRequest;
import com.digitalpetri.modbus.requests.WriteMultipleRegistersRequest;
import com.digitalpetri.modbus.requests.WriteSingleRegisterRequest;
import com.digitalpetri.modbus.responses.ReadHoldingRegistersResponse;
import com.digitalpetri.modbus.responses.WriteMultipleRegistersResponse;
import com.digitalpetri.modbus.responses.WriteSingleRegisterResponse;
import com.digitalpetri.modbus.slave.ModbusTcpSlave;
import com.digitalpetri.modbus.slave.ModbusTcpSlaveConfig;
import com.digitalpetri.modbus.slave.ServiceRequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutionException;

// Sets up a Modbus server to be used for testing power plant controllers which act as Modbus clients
public class ModbusClientController extends AbstractController{

    private final ModbusTcpSlaveConfig config = new ModbusTcpSlaveConfig.Builder().build();
    private final ModbusTcpSlave slave = new ModbusTcpSlave(config);

    private short[] modbusTable;
    /*
    Modbus Table (where N = inverter quantity):
    Registers 1->N: Inverter set point %
    Registers 101->1NN: Inverter power output KW
    Register 301: Plant power set point KW
    Register 302: Plant power output KW
     */

    public ModbusClientController(ControllerSettings settings, int invQuantity, double invPowerMax) {
        super(invQuantity, invPowerMax);

        modbusTable = new short[500];

        setupModbusServer();
    }

    double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        for (int i = 0; i < invQuantity; i++) {
            powerSetPoints[i] = modbusTable[i];

            modbusTable[100+i] = (short) (currentInverterPower[i] * 1000);
        }

        modbusTable[300] = (short) (plantPowerSetPoint * 1000);
        modbusTable[301] = (short) (currentPlantPower * 1000);

        return powerSetPoints;
    }

    public String getControllerName(){
        return "Modbus Client Controller";
    }


    private void setupModbusServer(){
        slave.setRequestHandler(new ServiceRequestHandler() {
            @Override
            public void onReadHoldingRegisters(ServiceRequestHandler.ServiceRequest<ReadHoldingRegistersRequest,
                    ReadHoldingRegistersResponse> service) {


                ReadHoldingRegistersRequest request = service.getRequest();

                ByteBuf registers = PooledByteBufAllocator.DEFAULT.buffer(request.getQuantity());

                for (int i = 0; i < request.getQuantity(); i++) {
                    registers.writeShort(modbusTable[request.getAddress() + i]);
                }

                service.sendResponse(new ReadHoldingRegistersResponse(registers));

                ReferenceCountUtil.release(request);
            }

            @Override
            public void onWriteSingleRegister(ServiceRequest<WriteSingleRegisterRequest,
                    WriteSingleRegisterResponse> service) {

                WriteSingleRegisterRequest request = service.getRequest();

                if (request.getAddress() < modbusTable.length) {
                    modbusTable[request.getAddress()] = (short) request.getValue();
                    service.sendResponse(new WriteSingleRegisterResponse(request.getAddress(),
                            request.getValue()));
                }
                else{
                    service.sendException(ExceptionCode.IllegalDataAddress);
                }

                ReferenceCountUtil.release(request);
            }

            @Override
            public void onWriteMultipleRegisters(ServiceRequest<WriteMultipleRegistersRequest,
                    WriteMultipleRegistersResponse> service) {

                WriteMultipleRegistersRequest request = service.getRequest();

                if (request.getAddress() + request.getQuantity() < modbusTable.length){
                    for (int i = 0; i < request.getQuantity(); i++) {
                        modbusTable[request.getAddress() + i] = request.getValues().readShort();
                    }
                    service.sendResponse(new WriteMultipleRegistersResponse(request.getAddress(),
                            request.getQuantity()));
                }
                else{
                    service.sendException(ExceptionCode.IllegalDataAddress);
                }

                ReferenceCountUtil.release(request);
            }
        });

        try {
            slave.bind("0.0.0.0", 50200).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void shutdownModbusServer(){
        slave.shutdown();
    }

}
