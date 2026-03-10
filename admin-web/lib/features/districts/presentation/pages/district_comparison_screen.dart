import 'package:flutter/material.dart';
import '../../../../ui/organisms/admin_sidebar.dart';
import '../../../../ui/organisms/admin_header.dart';

class DistrictComparisonScreen extends StatelessWidget {
  const DistrictComparisonScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          const AdminSidebar(currentPath: '/districts'),
          Expanded(
            child: Column(
              children: [
                const AdminHeader(title: 'Districts & Comparisons'),
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _buildComparisonChart(context),
                        const SizedBox(height: 24),
                        _buildDistrictTable(context),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildComparisonChart(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Actions per District',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 24),
            SizedBox(
              height: 200,
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.end,
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  _buildBar('Mitte', 0.8, Colors.green),
                  _buildBar('Nord', 0.5, Colors.blue),
                  _buildBar('Süd', 0.9, Colors.orange),
                  _buildBar('West', 0.3, Colors.red),
                  _buildBar('Ost', 0.6, Colors.purple),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildBar(String label, double heightFactor, Color color) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        Container(
          width: 40,
          height: 150 * heightFactor,
          decoration: BoxDecoration(
            color: color,
            borderRadius: const BorderRadius.vertical(top: Radius.circular(4)),
          ),
        ),
        const SizedBox(height: 8),
        Text(label, style: const TextStyle(fontSize: 12)),
      ],
    );
  }

  Widget _buildDistrictTable(BuildContext context) {
    return Card(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Padding(
            padding: EdgeInsets.all(24.0),
            child: Text(
              'District Details',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ),
          const Divider(height: 1),
          SizedBox(
            width: double.infinity,
            child: DataTable(
              columns: const [
                DataColumn(label: Text('District Name')),
                DataColumn(label: Text('Total Actions')),
                DataColumn(label: Text('Active Citizens')),
                DataColumn(label: Text('Green Coverage')),
                DataColumn(label: Text('Growth')),
              ],
              rows: [
                _buildDistrictRow('Mitte', '452', '120', '24%', '+5%'),
                _buildDistrictRow('Süd', '380', '95', '18%', '+12%'),
                _buildDistrictRow('Ost', '210', '45', '12%', '+2%'),
                _buildDistrictRow('Nord', '150', '30', '15%', '-1%'),
              ],
            ),
          ),
        ],
      ),
    );
  }

  DataRow _buildDistrictRow(String name, String actions, String citizens, String coverage, String growth) {
    return DataRow(cells: [
      DataCell(Text(name, style: const TextStyle(fontWeight: FontWeight.bold))),
      DataCell(Text(actions)),
      DataCell(Text(citizens)),
      DataCell(Text(coverage)),
      DataCell(Text(
        growth,
        style: TextStyle(
          color: growth.startsWith('+') ? Colors.green : Colors.red,
          fontWeight: FontWeight.bold,
        ),
      )),
    ]);
  }
}
