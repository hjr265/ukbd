package me.hjr265.ukbd.hid

val comboDescriptor = intArrayOf(
    0x05, 0x01,     // UsagePage(Generic Desktop[1])
    0x09, 0x02,     // UsageId(Mouse[2])
    0xa1, 0x01,     // Collection(Application)
    0x09, 0x01,     //     UsageId(Pointer[1])
    0xa1, 0x00,     //     Collection(Physical)
    0x85, 0x01,     //         ReportId(1)
    0x05, 0x09,     //         UsagePage(Button[9])
    0x19, 0x01,     //         UsageIdMin([1])
    0x29, 0x03,     //         UsageIdMax([3])
    0x15, 0x00,     //         LogicalMinimum(0)
    0x25, 0x01,     //         LogicalMaximum(1)
    0x95, 0x03,     //         ReportCount(3)
    0x75, 0x01,     //         ReportSize(1)
    0x81, 0x02,     //         Input(Data, Variable, Absolute)
    0x95, 0x01,     //         ReportCount(1)
    0x75, 0x05,     //         ReportSize(5)
    0x81, 0x03,     //         Input(Constant, Variable, Absolute)
    0x05, 0x01,     //         UsagePage(Generic Desktop[1])
    0x09, 0x30,     //         UsageId(X[48])
    0x09, 0x31,     //         UsageId(Y[49])
    0x09, 0x38,     //         UsageId(Wheel[56])
    0x15, 0x81,     //         LogicalMinimum(-127)
    0x25, 0x7f,     //         LogicalMaximum(127)
    0x75, 0x08,     //         ReportSize(8)
    0x95, 0x03,     //         ReportCount(3)
    0x81, 0x06,     //         Input(Data, Variable, Relative)
    0xc0,           //     EndCollection()
    0xc0,           // EndCollection()

    0x05, 0x01,     // UsagePage(Generic Desktop[1])
    0x09, 0x06,     // UsageId([6])
    0xa1, 0x01,     // Collection(Application)
    0x85, 0x02,     //     ReportId(2)
    0x05, 0x07,     //     UsagePage(Keyboard[7])
    0x19, 0xE0,     //     UsageIdMin([224])
    0x29, 0xE7,     //     UsageIdMax([231])
    0x15, 0x00,     //     LogicalMinimum(0)
    0x25, 0x01,     //     LogicalMaximum(1)
    0x75, 0x01,     //     ReportSize(1)
    0x95, 0x08,     //     ReportCount(8)
    0x81, 0x02,     //     Input(Data, Variable, Absolute)

    0x95, 0x01,     //     ReportCount(1)
    0x75, 0x08,     //     ReportSize(8)
    0x81, 0x01,     //     Input(Constant)

    0x95, 0x05,     //     ReportCount(5)
    0x75, 0x01,     //     ReportSize(1)
    0x05, 0x08,     //     UsagePage(LEDs[8])
    0x19, 0x01,     //     UsageIdMin([1])
    0x29, 0x05,     //     UsageIdMax([5])
    0x91, 0x02,     //     Output(Data, Variable, Absolute)
    0x95, 0x01,     //     ReportCount(1)
    0x75, 0x03,     //     ReportSize(3)
    0x91, 0x01,     //     Output(Data, Variable, Absolute)

    0x95, 0x06,     //     ReportCount(6)
    0x75, 0x08,     //     ReportSize(8)
    0x15, 0x00,     //     LogicalMinimum(0)
    0x25, 0x65,     //     LogicalMaximum(101)
    0x05, 0x07,     //     UsagePage(Keyboard/Keypad[7])
    0x19, 0x00,     //     UsageIdMin([0])
    0x29, 0x65,     //     UsageIdMax([101])
    0x81, 0x00,     //     Input(Data, Array)
    0xc0            // EndCollection()
).map { it.toByte() }.toByteArray()